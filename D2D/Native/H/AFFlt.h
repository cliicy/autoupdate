#ifndef _AFFLT_H_
#define _AFFLT_H_

#define CA_FLT_DEV_BASE               0xB000

#define CAFLTCTL_GET_BITMAP \
    CTL_CODE(CA_FLT_DEV_BASE, 0x0,  METHOD_OUT_DIRECT, FILE_ANY_ACCESS )

#define CAFLTCTL_GET_BLOCK_SIZE \
    CTL_CODE( CA_FLT_DEV_BASE, 0x1, METHOD_OUT_DIRECT, FILE_ANY_ACCESS )
	
#define CAFLTCTL_VOLUME_BLOCK_SIZE_CHANGE\
	CTL_CODE( CA_FLT_DEV_BASE, 0x2, METHOD_IN_DIRECT, FILE_ANY_ACCESS  )

#define CAFLTCTL_SYNC_SNAPSHOT_CHANGES_TO_VOLUME_BITMAP\
	CTL_CODE( CA_FLT_DEV_BASE, 0x3, METHOD_IN_DIRECT, FILE_ANY_ACCESS  )
	
#define CAFLTCTL_REPLICATION_SUCCESS\
	CTL_CODE( CA_FLT_DEV_BASE, 0x4, METHOD_IN_DIRECT, FILE_ANY_ACCESS  )
	
#define CAFLTCTL_PREPARE_FOR_SNAPSHOT\
	CTL_CODE( CA_FLT_DEV_BASE, 0x5, METHOD_IN_DIRECT, FILE_ANY_ACCESS  )

//#define CAFLTCTL_PREPARE_FOR_UNINSTALL\
//	CTL_CODE( CA_FLT_DEV_BASE, 0x6, METHOD_IN_DIRECT, FILE_ANY_ACCESS  )

#define CAFLTCTL_STOP_MONITORING_VOLUME\
	CTL_CODE(CA_FLT_DEV_BASE, 0x7, METHOD_IN_DIRECT, FILE_ANY_ACCESS)

#define CAFLTCTL_START_MONITORING\
	CTL_CODE(CA_FLT_DEV_BASE, 0x8, METHOD_IN_DIRECT, FILE_ANY_ACCESS)

#define CAFLTCTL_STOP_MONITORING_ALL\
	CTL_CODE(CA_FLT_DEV_BASE, 0x9, METHOD_IN_DIRECT, FILE_ANY_ACCESS)


#define AFFLTDEV_NAME     L"AFFltMgr"
#define AFFilter          L"\\\\.\\"AFFLTDEV_NAME
#define AFFLTDriver_Name  L"AFFlt"

#define AFFLT_RESYNC_BACKUP_DEVICE_NAME                                   L"DeviceListForReSync"
#define AFFLT_LEGACY_MODE                                                 L"LegacyMode"
#define AFFLT_BLOCK_SIZE                                                  L"BlockSize"
#define AFFLT_CHECKBITMAP_TIMEOUT                                         L"BitmapTimeout"

typedef struct _AFFLT_START_MONITOR_
{
	ULONG ulSize;//the size of the structure
	ULONG ulDeviceNameOffset;//it is can be \Device\HarddiskVolume1, or  \??\Volume{c5ae4daf-d95e-11e2-bab7-005056c00008}, or \??\c:
	ULONG ulDeviceNameLength;
	ULONG ulDriverNameOffset;// The name of the driver which is in the device stack, default it is VolSnap; if it is 0, that means use the default driver VolSnap
	ULONG ulDriverNameLength;
}AFFLT_START_MONITOR, *PAFFLT_START_MONITOR;

/*---

CAFLTCTL_START_MONITORING

Input, if NULL, will mountor all the non-removable volume in the system.
       if NON-NULL, it is AFFLT_START_MONITOR.
--*/


/*---
CAFLTCTL_GET_BITMAP

the input output, please reference FSCTL_GET_VOLUME_BITMAP
--*/

/*---
CAFLTCTL_SYNC_SNAPSHOT_CHANGES_TO_VOLUME_BITMAP

Input, PVOLUME_BITMAP_BUFFER

--*/

#endif //_AFFLT_H_

