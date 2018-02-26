 // The following are the categories of events.
//
//  Values are 32 bit values laid out as follows:
//
//   3 3 2 2 2 2 2 2 2 2 2 2 1 1 1 1 1 1 1 1 1 1
//   1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0
//  +---+-+-+-----------------------+-------------------------------+
//  |Sev|C|R|     Facility          |               Code            |
//  +---+-+-+-----------------------+-------------------------------+
//
//  where
//
//      Sev - is the severity code
//
//          00 - Success
//          01 - Informational
//          10 - Warning
//          11 - Error
//
//      C - is the Customer code flag
//
//      R - is a reserved bit
//
//      Facility - is the facility code
//
//      Code - is the facility's status code
//
//
// Define the facility codes
//
#define FACILITY_SYSTEM                  0x0
#define FACILITY_STUBS                   0x3
#define FACILITY_RUNTIME                 0x2
#define FACILITY_IO_ERROR_CODE           0x4


//
// Define the severity codes
//
#define STATUS_SEVERITY_WARNING          0x2
#define STATUS_SEVERITY_SUCCESS          0x0
#define STATUS_SEVERITY_INFORMATIONAL    0x1
#define STATUS_SEVERITY_ERROR            0x3


//
// MessageId: UDP_EVENT_CATEGORY_UNKNOWN_JOB
//
// MessageText:
//
// Unknown
//
#define UDP_EVENT_CATEGORY_UNKNOWN_JOB   ((WORD)0x00000001L)

//
// MessageId: UDP_EVENT_CATEGORY_COMMON
//
// MessageText:
//
// Common
//
#define UDP_EVENT_CATEGORY_COMMON        ((WORD)0x00000002L)

//
// MessageId: UDP_EVENT_CATEGORY_BACKUP_JOB
//
// MessageText:
//
// Backup
//
#define UDP_EVENT_CATEGORY_BACKUP_JOB    ((WORD)0x00000003L)

//
// MessageId: UDP_EVENT_CATEGORY_RESTORE_JOB
//
// MessageText:
//
// Restore
//
#define UDP_EVENT_CATEGORY_RESTORE_JOB   ((WORD)0x00000004L)

//
// MessageId: UDP_EVENT_CATEGORY_ARCHIEVE_JOB
//
// MessageText:
//
// File Copy
//
#define UDP_EVENT_CATEGORY_ARCHIEVE_JOB  ((WORD)0x00000005L)

//
// MessageId: UDP_EVENT_CATEGORY_FILE_ARCHIVE_JOB
//
// MessageText:
//
// File Archive
//
#define UDP_EVENT_CATEGORY_FILE_ARCHIVE_JOB ((WORD)0x00000006L)

//
// MessageId: UDP_EVENT_CATEGORY_FILE_COPY_DELETE_JOB
//
// MessageText:
//
// File Copy Delete
//
#define UDP_EVENT_CATEGORY_FILE_COPY_DELETE_JOB ((WORD)0x00000007L)

//
// MessageId: UDP_EVENT_CATEGORY_FILE_COPY_RESTORE_JOB
//
// MessageText:
//
// File Copy Restore
//
#define UDP_EVENT_CATEGORY_FILE_COPY_RESTORE_JOB ((WORD)0x00000008L)

//
// MessageId: UDP_EVENT_CATEGORY_FILE_COPY_PURGE_JOB
//
// MessageText:
//
// File Copy Purge
//
#define UDP_EVENT_CATEGORY_FILE_COPY_PURGE_JOB ((WORD)0x00000009L)

//
// MessageId: UDP_EVENT_CATEGORY_FILE_COPY_CATALOG_SYNC_JOB
//
// MessageText:
//
// File Copy Catalog Sync
//
#define UDP_EVENT_CATEGORY_FILE_COPY_CATALOG_SYNC_JOB ((WORD)0x0000000AL)

//
// MessageId: UDP_EVENT_CATEGORY_COPY_SESSION_JOB
//
// MessageText:
//
// Copy Recovery Point
//
#define UDP_EVENT_CATEGORY_COPY_SESSION_JOB ((WORD)0x0000000BL)

//
// MessageId: UDP_EVENT_CATEGORY_CATALOG_JOB
//
// MessageText:
//
// File System Catalog
//
#define UDP_EVENT_CATEGORY_CATALOG_JOB   ((WORD)0x0000000CL)

//
// MessageId: UDP_EVENT_CATEGORY_VM_CATALOG_JOB
//
// MessageText:
//
// File System Catalog(VM)
//
#define UDP_EVENT_CATEGORY_VM_CATALOG_JOB ((WORD)0x0000000DL)

//
// MessageId: UDP_EVENT_CATEGORY_OD_CATALOG_JOB
//
// MessageText:
//
// On - demand File System Catalog
//
#define UDP_EVENT_CATEGORY_OD_CATALOG_JOB ((WORD)0x0000000EL)

//
// MessageId: UDP_EVENT_CATEGORY_GRT_CATALOG_JOB
//
// MessageText:
//
// Exchange GRT Catalog
//
#define UDP_EVENT_CATEGORY_GRT_CATALOG_JOB ((WORD)0x0000000FL)

//
// MessageId: UDP_EVENT_CATEGORY_VM_RECOVERY_JOG
//
// MessageText:
//
// Recover VM
//
#define UDP_EVENT_CATEGORY_VM_RECOVERY_JOG ((WORD)0x00000010L)

//
// MessageId: UDP_EVENT_CATEGORY_VSB_JOB
//
// MessageText:
//
// Virtual Standby
//
#define UDP_EVENT_CATEGORY_VSB_JOB       ((WORD)0x00000011L)

//
// MessageId: UDP_EVENT_CATEGORY_MERGE_JOB
//
// MessageText:
//
// Merge
//
#define UDP_EVENT_CATEGORY_MERGE_JOB     ((WORD)0x00000012L)

//
// MessageId: UDP_EVENT_CATEGORY_RPS_MERGE_JOB
//
// MessageText:
//
// Merge on RPS
//
#define UDP_EVENT_CATEGORY_RPS_MERGE_JOB ((WORD)0x00000013L)

//
// MessageId: UDP_EVENT_CATEGORY_REPLICATION_OUT_JOB
//
// MessageText:
//
// Replication(Out)
//
#define UDP_EVENT_CATEGORY_REPLICATION_OUT_JOB ((WORD)0x00000014L)

//
// MessageId: UDP_EVENT_CATEGORY_REPLICATION_IN_JOB
//
// MessageText:
//
// Replication(In)
//
#define UDP_EVENT_CATEGORY_REPLICATION_IN_JOB ((WORD)0x00000015L)

//
// MessageId: UDP_EVENT_CATEGORY_JUMPSTART_OUT_JOB
//
// MessageText:
//
// RPS Jumpstart(Out)
//
#define UDP_EVENT_CATEGORY_JUMPSTART_OUT_JOB ((WORD)0x00000016L)

//
// MessageId: UDP_EVENT_CATEGORY_JUMPSTART_IN_JOB
//
// MessageText:
//
// RPS Jumpstart(In)
//
#define UDP_EVENT_CATEGORY_JUMPSTART_IN_JOB ((WORD)0x00000017L)

//
// MessageId: UDP_EVENT_CATEGORY_BMR_JOB
//
// MessageText:
//
// BMR
//
#define UDP_EVENT_CATEGORY_BMR_JOB       ((WORD)0x00000018L)

//
// MessageId: UDP_EVENT_CATEGORY_START_INSTANTVM_JOB
//
// MessageText:
//
// Start ^AU_ProductName_IVM_SHORT^
//
#define UDP_EVENT_CATEGORY_START_INSTANTVM_JOB ((WORD)0x00000019L)

//
// MessageId: UDP_EVENT_CATEGORY_STOP_INSTANTVM_JOB
//
// MessageText:
//
// Stop ^AU_ProductName_IVM_SHORT^
//
#define UDP_EVENT_CATEGORY_STOP_INSTANTVM_JOB ((WORD)0x0000001AL)

//
// MessageId: UDP_EVENT_CATEGORY_START_INSTANTVHD_JOB
//
// MessageText:
//
// Start ^AU_ProductName_IVHD_SHORT^
//
#define UDP_EVENT_CATEGORY_START_INSTANTVHD_JOB ((WORD)0x0000001BL)

//
// MessageId: UDP_EVENT_CATEGORY_STOP_INSTANTVHD_JOB
//
// MessageText:
//
// Stop ^AU_ProductName_IVHD_SHORT^
//
#define UDP_EVENT_CATEGORY_STOP_INSTANTVHD_JOB ((WORD)0x0000001CL)

//
// MessageId: UDP_EVENT_CATEGORY_ARCHIVE_TO_TAPE_JOB
//
// MessageText:
//
// Archive To Tape
//
#define UDP_EVENT_CATEGORY_ARCHIVE_TO_TAPE_JOB ((WORD)0x0000001DL)

//
// MessageId: UDP_EVENT_CATEGORY_PURGE_JOB
//
// MessageText:
//
// Purge
//
#define UDP_EVENT_CATEGORY_PURGE_JOB     ((WORD)0x0000001EL)

 // The following are the message definitions.
 // The following are the message definitions for Afstor
//
// MessageId: AFRES_AFSTOR_VHD_RET_CHKSUM_MISMATCH
//
// MessageText:
//
// CHECKSUM VALUE DOES NOT MATCH%0
//
#define AFRES_AFSTOR_VHD_RET_CHKSUM_MISMATCH ((DWORD)0x40060001L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_BAD_DISK_NAME
//
// MessageText:
//
// INCORRECT DISK NAME%0
//
#define AFRES_AFSTOR_VHD_RET_BAD_DISK_NAME ((DWORD)0x40060002L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_BAD_DISK_FILE
//
// MessageText:
//
// INCORRECT DISK FILE%0
//
#define AFRES_AFSTOR_VHD_RET_BAD_DISK_FILE ((DWORD)0x40060003L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_BAD_DESC_FILE
//
// MessageText:
//
// INCORRECT DESCRIPTION FILE%0
//
#define AFRES_AFSTOR_VHD_RET_BAD_DESC_FILE ((DWORD)0x40060004L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_BAD_META_FILE
//
// MessageText:
//
// INCORRECT METADATA FILE%0
//
#define AFRES_AFSTOR_VHD_RET_BAD_META_FILE ((DWORD)0x40060005L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_NO_DEST_FOUND
//
// MessageText:
//
// DESTINATION NOT FOUND%0
//
#define AFRES_AFSTOR_VHD_RET_NO_DEST_FOUND ((DWORD)0x40060006L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_BAD_COMMAND
//
// MessageText:
//
// INCORRECT COMMAND%0
//
#define AFRES_AFSTOR_VHD_RET_BAD_COMMAND ((DWORD)0x40060007L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_NO_SPARSE_SUPPORT
//
// MessageText:
//
// SPARSE FORMAT NOT SUPPORTED%0
//
#define AFRES_AFSTOR_VHD_RET_NO_SPARSE_SUPPORT ((DWORD)0x40060008L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FILE_SIZE_LIMITED
//
// MessageText:
//
// MAXIMUM FILE SIZE EXCEEDED%0
//
#define AFRES_AFSTOR_VHD_RET_FILE_SIZE_LIMITED ((DWORD)0x40060009L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_INVALID_FOOTER
//
// MessageText:
//
// INVALID FOOTER%0
//
#define AFRES_AFSTOR_VHD_RET_INVALID_FOOTER ((DWORD)0x4006000AL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_INVALID_PARAMETER
//
// MessageText:
//
// INVALID PARAMETER%0
//
#define AFRES_AFSTOR_VHD_RET_INVALID_PARAMETER ((DWORD)0x4006000BL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_INVALID_DISK_TYPE
//
// MessageText:
//
// INVALID DISK TYPE%0
//
#define AFRES_AFSTOR_VHD_RET_INVALID_DISK_TYPE ((DWORD)0x4006000CL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_ROLLBACK_FAILED
//
// MessageText:
//
// ROOLBACK FAILED%0
//
#define AFRES_AFSTOR_VHD_RET_ROLLBACK_FAILED ((DWORD)0x4006000DL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FOUND_NO_PARENT
//
// MessageText:
//
// CAN NOT FOUND PARENT DISK%0
//
#define AFRES_AFSTOR_VHD_RET_FOUND_NO_PARENT ((DWORD)0x4006000EL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FOUND_NO_DISK
//
// MessageText:
//
// CAN NOT FOUND DISK%0
//
#define AFRES_AFSTOR_VHD_RET_FOUND_NO_DISK ((DWORD)0x4006000FL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_NO_ENOUGH_MEMORY
//
// MessageText:
//
// MEMORY NOT ENOUGH%0
//
#define AFRES_AFSTOR_VHD_RET_NO_ENOUGH_MEMORY ((DWORD)0x40060010L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_INVALID_DESTINATION
//
// MessageText:
//
// INVALID DESTINATION%0
//
#define AFRES_AFSTOR_VHD_RET_INVALID_DESTINATION ((DWORD)0x40060011L)

//
// MessageId: AFRES_AFSTOR_DENY_OPENFILE
//
// MessageText:
//
// Attempt to open file %1!s! failed. Another process (such as ^AU_ProductName_HA_OLD^) may currently be using this file.%0
//
#define AFRES_AFSTOR_DENY_OPENFILE       ((DWORD)0xC0060012L)

//
// MessageId: AFRES_AFSTOR_FAIL_OPFILE
//
// MessageText:
//
// Operations (create, close, read, write) on file %1!s! failed, Error code:%2!d!,[%3!s!].%0
//
#define AFRES_AFSTOR_FAIL_OPFILE         ((DWORD)0xC0060013L)

//
// MessageId: AFRES_AFSTOR_INVALID_DATA
//
// MessageText:
//
// An invalid data block is detected.%0
//
#define AFRES_AFSTOR_INVALID_DATA        ((DWORD)0xC0060014L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_PREFLIGHT_CHECK_FAIL
//
// MessageText:
//
// PREFLIGHT CHECK (PFC) FAILED%0
//
#define AFRES_AFSTOR_VHD_RET_PREFLIGHT_CHECK_FAIL ((DWORD)0x40060015L)

//
// MessageId: AFRES_AFSTOR_RENAME_FAIL
//
// MessageText:
//
// During the merge operation, the folder rename from %1!s! to %2!s! failed. Error code: %3!d!, [%4!s!].%0
//
#define AFRES_AFSTOR_RENAME_FAIL         ((DWORD)0xC0060016L)

//
// MessageId: AFRES_AFSTOR_CHECK_PARENT_FAIL
//
// MessageText:
//
// An invalid parent disk [%1!s!] is detected.%0
//
#define AFRES_AFSTOR_CHECK_PARENT_FAIL   ((DWORD)0xC0060017L)

//
// MessageId: AFRES_AFSTOR_RENAME_FILE_FAIL
//
// MessageText:
//
// During the merge operation, failed to rename file from %1!s! to %2!s!. Error code: %3!d!, [%4!s!].%0
//
#define AFRES_AFSTOR_RENAME_FILE_FAIL    ((DWORD)0xC0060018L)

//
// MessageId: AFRES_AFSTOR_DEL_FILE_FAIL
//
// MessageText:
//
// Failed to delete file %1!s!. Error code: %2!d!, [%3!s!].%0
//
#define AFRES_AFSTOR_DEL_FILE_FAIL       ((DWORD)0xC0060019L)

//
// MessageId: AFRES_AFSTOR_MOVE_FILE_FAIL
//
// MessageText:
//
// During the merge operation, failed to move file from %1!s! to %2!s!. Error code: %3!d!, [%4!s!].%0
//
#define AFRES_AFSTOR_MOVE_FILE_FAIL      ((DWORD)0xC006001AL)

//
// MessageId: AFRES_AFSTOR_MERGE_DISK_FAIL
//
// MessageText:
//
// Failed to merge virtual disk. System error=[%1!s!].%0
//
#define AFRES_AFSTOR_MERGE_DISK_FAIL     ((DWORD)0xC006001BL)

//
// MessageId: AFRES_AFSTOR_MERGE_DISK_FAIL_INTERNAL
//
// MessageText:
//
// Failed to merge virtual disk. Internal error=[%1!s!].%0
//
#define AFRES_AFSTOR_MERGE_DISK_FAIL_INTERNAL ((DWORD)0xC006001CL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_INVALID_ENCINFO
//
// MessageText:
//
// INVALID ENCRYPTION INFORMATION%0
//
#define AFRES_AFSTOR_VHD_RET_INVALID_ENCINFO ((DWORD)0x4006001DL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_ENCDATA_ERROR
//
// MessageText:
//
// FAILED TO ENCRYPT%0
//
#define AFRES_AFSTOR_VHD_RET_ENCDATA_ERROR ((DWORD)0x4006001EL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_PURGE_NOT_ALLOWED
//
// MessageText:
//
// PURGE IS NOT ALLOWED%0
//
#define AFRES_AFSTOR_VHD_RET_PURGE_NOT_ALLOWED ((DWORD)0x4006001FL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_UNCOMPRESS_ERROR
//
// MessageText:
//
// FAILED TO UNCOMPRESS%0
//
#define AFRES_AFSTOR_VHD_RET_UNCOMPRESS_ERROR ((DWORD)0x40060020L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_UNKNOWN_INTERNAL_ERROR
//
// MessageText:
//
// UNKNOWN INTERNAL ERROR%0
//
#define AFRES_AFSTOR_VHD_RET_UNKNOWN_INTERNAL_ERROR ((DWORD)0x40060021L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_INIT_DISK_FORMAT_FAIL
//
// MessageText:
//
// FAILED TO INIT DISK FORMAT%0
//
#define AFRES_AFSTOR_VHD_RET_INIT_DISK_FORMAT_FAIL ((DWORD)0x40060022L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_INVALID_DATA_STORE_CONFIG
//
// MessageText:
//
// INVALID DATA STORE CONFIGURATION%0
//
#define AFRES_AFSTOR_VHD_RET_INVALID_DATA_STORE_CONFIG ((DWORD)0x40060023L)

//
// MessageId: AFRES_AFSTOR_MERGE_GENERICAL_FAIL
//
// MessageText:
//
// Failed to merge data.%0
//
#define AFRES_AFSTOR_MERGE_GENERICAL_FAIL ((DWORD)0xC0060024L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FAILED_2_WRITE_DATA_INT
//
// MessageText:
//
// Failed to write data. Internal error=[%1!s!].%0
//
#define AFRES_AFSTOR_VHD_RET_FAILED_2_WRITE_DATA_INT ((DWORD)0xC0060025L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FAILED_2_WRITE_DATA_SYS
//
// MessageText:
//
// Failed to write data. System error=[%1!s!].%0
//
#define AFRES_AFSTOR_VHD_RET_FAILED_2_WRITE_DATA_SYS ((DWORD)0xC0060026L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FAILED_2_READ_DATA_INT
//
// MessageText:
//
// Failed to read data. Internal error=[%1!s!].%0
//
#define AFRES_AFSTOR_VHD_RET_FAILED_2_READ_DATA_INT ((DWORD)0xC0060027L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FAILED_2_READ_DATA_SYS
//
// MessageText:
//
// Failed to read data. System error=[%1!s!].%0
//
#define AFRES_AFSTOR_VHD_RET_FAILED_2_READ_DATA_SYS ((DWORD)0xC0060028L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FAILED_2_OPEN_VD_INT
//
// MessageText:
//
// Failed to open the virtual disk. Internal error=[%1!s!].%0
//
#define AFRES_AFSTOR_VHD_RET_FAILED_2_OPEN_VD_INT ((DWORD)0xC0060029L)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FAILED_2_OPEN_VD_SYS
//
// MessageText:
//
// Failed to open the virtual disk. System error=[%1!s!].%0
//
#define AFRES_AFSTOR_VHD_RET_FAILED_2_OPEN_VD_SYS ((DWORD)0xC006002AL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_FAILED_2_CHECK_PREV_MERGER
//
// MessageText:
//
// FAILED TO ROLLBACK THE PREVIOUS MERGER JOB%0
//
#define AFRES_AFSTOR_VHD_RET_FAILED_2_CHECK_PREV_MERGER ((DWORD)0x4006002BL)

//
// MessageId: AFRES_AFSTOR_VHD_RET_DECRYPT_ERROR
//
// MessageText:
//
// FAILED TO DECRYPT%0
//
#define AFRES_AFSTOR_VHD_RET_DECRYPT_ERROR ((DWORD)0x4006002CL)

//
// MessageId: AFRES_AFSTOR_E1000E_WARNING
//
// MessageText:
//
// An Intel(R) 82574 network adapter has been detected. The TCP segmentation offload (TSO) feature (also known as the Large Send Offload) of this adapter, has been disabled to avoid any potential data corruption.%0
//
#define AFRES_AFSTOR_E1000E_WARNING      ((DWORD)0x8006002DL)

//
// MessageId: AFRES_AFSTOR_E1000E_ERROR
//
// MessageText:
//
// An Intel(R) 82574 network adapter has been detected. This adapter can cause data corruption. Change the network adapter or manually disable the TCP segmentation offload (TSO) feature of this adapter (also known as Large Send Offload).%0
//
#define AFRES_AFSTOR_E1000E_ERROR        ((DWORD)0x8006002EL)

//
// MessageId: AFRES_NOT_SUPPORT_32BIT_PROXY
//
// MessageText:
//
// 32bit proxy is not supported any longer.%0
//
#define AFRES_NOT_SUPPORT_32BIT_PROXY    ((DWORD)0xC006002FL)

 // The following are the message definitions for Appliance
//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_AS_PREDNS_ALTDNS_SAME
//
// MessageText:
//
// The alternate DNS server cannot be the same as the preferred DNS server. Please enter a different IP address for the alternate DNS server.%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_AS_PREDNS_ALTDNS_SAME ((DWORD)0xC01D0001L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_AS_HOSTNAME_EXCEED_15
//
// MessageText:
//
// The NetBIOS name of the computer name is limited to 15 characters. As a result, the NetBIOS name will be shortened to [%1!s!]. Click Yes to continue with the shortened name or click No to input a new hostname.%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_AS_HOSTNAME_EXCEED_15 ((DWORD)0xC01D0002L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_AS_EULA_POWER_OFF
//
// MessageText:
//
// Are you sure you want to cancel the appliance configuration? If you select Yes, the appliance will power off.%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_AS_EULA_POWER_OFF ((DWORD)0xC01D0003L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_AS_MULTIPLE_INSTANCE
//
// MessageText:
//
// Only single instance of the application is allowed to run.%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_AS_MULTIPLE_INSTANCE ((DWORD)0xC01D0004L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_ICON_NUMBER_CONNECTED_STR
//
// MessageText:
//
// Connected%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_ICON_NUMBER_CONNECTED_STR ((DWORD)0xC01D0005L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_ICON_NUMBER_NOT_CONNECTED_STR
//
// MessageText:
//
// Not Connected%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_ICON_NUMBER_NOT_CONNECTED_STR ((DWORD)0xC01D0006L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_IP_NOT_ASSIGNED
//
// MessageText:
//
// None Assigned%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_IP_NOT_ASSIGNED ((DWORD)0xC01D0007L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_IP_VIA_DHCP
//
// MessageText:
//
// Automatic via DHCP%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_IP_VIA_DHCP ((DWORD)0xC01D0008L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_IP_MANUAL_ASSIGNED
//
// MessageText:
//
// Manually Assigned%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_IP_MANUAL_ASSIGNED ((DWORD)0xC01D0009L)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_ERROR
//
// MessageText:
//
// Error%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_ERROR ((DWORD)0xC01D000AL)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_WARNING
//
// MessageText:
//
// Warning%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_WARNING ((DWORD)0xC01D000BL)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_AS_DIALOG_CAPTION
//
// MessageText:
//
// ^AU_ProductName_UDP_SHORT^ Appliance%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_AS_DIALOG_CAPTION ((DWORD)0xC01D000CL)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_EDIT
//
// MessageText:
//
// Edit%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_EDIT ((DWORD)0xC01D000DL)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_HOSTNAMEINVALID
//
// MessageText:
//
// The computer name can contain only alphabetical characters (A-Z or a-z), numeric characters (0-9) and the minus sign (-).%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_HOSTNAMEINVALID ((DWORD)0xC01D000EL)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_APPLIANCE_REMINDER
//
// MessageText:
//
// Starting ^AU_ProductName_UDP_SHORT^ Appliance Configuration Wizard%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_APPLIANCE_REMINDER ((DWORD)0xC01D000FL)

//
// MessageId: AFRES_UDP_APPLIANCE_AS_WIZARD_APPLIANCE_REMINDER_WAIT
//
// MessageText:
//
// please wait%0
//
#define AFRES_UDP_APPLIANCE_AS_WIZARD_APPLIANCE_REMINDER_WAIT ((DWORD)0xC01D0010L)

 // The following are the message definitions for Archive
//
// MessageId: AFRES_AFARCHIVE_CREATE_POLICY_ERROR
//
// MessageText:
//
// Failed to Create %1!s! Policy Error = %2!s!.%0
//
#define AFRES_AFARCHIVE_CREATE_POLICY_ERROR ((DWORD)0xC0070001L)

//
// MessageId: AFRES_AFARCHIVE_TOTAL_THROUGHPUT
//
// MessageText:
//
// Total processed data size is %1!s!, elapsed time %2!s!, throughput %3!s!/Min.%0
//
#define AFRES_AFARCHIVE_TOTAL_THROUGHPUT ((DWORD)0x40070002L)

//
// MessageId: AFRES_AFARCHIVE_BACKUP_STATISTICS
//
// MessageText:
//
// Total files copied :%1!d!.%0
//
#define AFRES_AFARCHIVE_BACKUP_STATISTICS ((DWORD)0x40070003L)

//
// MessageId: AFRES_AFARCHIVE_CONVERT_JOB_SCRIPT_FAIL
//
// MessageText:
//
// Failed to convert xml to job script (Error Code = %1!d!).%0
//
#define AFRES_AFARCHIVE_CONVERT_JOB_SCRIPT_FAIL ((DWORD)0xC0070004L)

//
// MessageId: AFRES_AFARCHIVE_METHOD_CHANGE_FIRST_JOB
//
// MessageText:
//
// Next scheduled %1!s! job will be converted to full as it is the first %2!s! job for the volume (%3!s!).%0
//
#define AFRES_AFARCHIVE_METHOD_CHANGE_FIRST_JOB ((DWORD)0x80070005L)

//
// MessageId: AFRES_AFARCHIVE_ARCHIVE_CANCELLED
//
// MessageText:
//
// User canceled the %1!s! job.%0
//
#define AFRES_AFARCHIVE_ARCHIVE_CANCELLED ((DWORD)0x80070006L)

//
// MessageId: AFRES_AFARCHIVE_PURGE_STARTED
//
// MessageText:
//
// File Copy Purge job has started.%0
//
#define AFRES_AFARCHIVE_PURGE_STARTED    ((DWORD)0x40070007L)

//
// MessageId: AFRES_AFARCHIVE_PURGE_END
//
// MessageText:
//
// File Copy Purge job has ended.%0
//
#define AFRES_AFARCHIVE_PURGE_END        ((DWORD)0x40070008L)

//
// MessageId: AFRES_AFARCHIVE_PURGE_STATS
//
// MessageText:
//
// Total files Purged is : %1!d!.%0
//
#define AFRES_AFARCHIVE_PURGE_STATS      ((DWORD)0x40070009L)

//
// MessageId: AFRES_AFARCHIVE_JOB_STARTED
//
// MessageText:
//
// %1!s! job started running.%0
//
#define AFRES_AFARCHIVE_JOB_STARTED      ((DWORD)0x4007000AL)

//
// MessageId: AFRES_AFARCHIVE_BACKUP_COMPLETED
//
// MessageText:
//
// %1!s! job has completed.%0
//
#define AFRES_AFARCHIVE_BACKUP_COMPLETED ((DWORD)0x4007000BL)

//
// MessageId: AFRES_AFARCHIVE_ARCHIVE_FAILED
//
// MessageText:
//
// %1!s! job has failed.%0
//
#define AFRES_AFARCHIVE_ARCHIVE_FAILED   ((DWORD)0xC007000CL)

//
// MessageId: AFRES_AFARCHIVE_ARCHIVE_INCOMPLETE
//
// MessageText:
//
// %1!s! job is incomplete.%0
//
#define AFRES_AFARCHIVE_ARCHIVE_INCOMPLETE ((DWORD)0x8007000DL)

//
// MessageId: AFRES_AFARCHIVE_ARCHIVE_CONVERT_JOB
//
// MessageText:
//
// Converting your scheduled %1!s! job to full because %2!s! destination has changed or this job is the first %3!s! job.%0
//
#define AFRES_AFARCHIVE_ARCHIVE_CONVERT_JOB ((DWORD)0x8007000EL)

//
// MessageId: AFRES_AFARCHIVE_SKIP_INCREMENTAL_COPY
//
// MessageText:
//
// Will be skipping incremental %1!s! because %2!s! destination has changed, incremental %3!s! will be automatically convert to a full %4!s! in next backup.%0
//
#define AFRES_AFARCHIVE_SKIP_INCREMENTAL_COPY ((DWORD)0x8007000FL)

//
// MessageId: AFRES_AFARCHIVE_VOL_MOUNT_FAILED
//
// MessageText:
//
// Mount virtual disk failed. Error %1!d!.%0
//
#define AFRES_AFARCHIVE_VOL_MOUNT_FAILED ((DWORD)0xC0070010L)

//
// MessageId: AFRES_AFARCHIVE_LOG_BACUP_SESSION
//
// MessageText:
//
// Will perform %1!s! from session %2!s!.%0
//
#define AFRES_AFARCHIVE_LOG_BACUP_SESSION ((DWORD)0x40070011L)

//
// MessageId: AFRES_AFARCHIVE_LOG_VOLUME_NAME
//
// MessageText:
//
// Will perform %1!s! for Volume %2!s!.%0
//
#define AFRES_AFARCHIVE_LOG_VOLUME_NAME  ((DWORD)0x40070012L)

//
// MessageId: AFRES_AFARCHIVE_FSDEST_DETAILS
//
// MessageText:
//
// %1!s! destination for this job is %2!s!.%0
//
#define AFRES_AFARCHIVE_FSDEST_DETAILS   ((DWORD)0x40070013L)

//
// MessageId: AFRES_AFARCHIVE_SESSION_NO
//
// MessageText:
//
// Backup Session No for this %1!s! job is %2!d!.%0
//
#define AFRES_AFARCHIVE_SESSION_NO       ((DWORD)0x40070014L)

//
// MessageId: AFRES_AFARCHIVE_INITCLOUDSESSION_FAILURE
//
// MessageText:
//
// Unable to reach %1!s! destination.%0
//
#define AFRES_AFARCHIVE_INITCLOUDSESSION_FAILURE ((DWORD)0xC0070015L)

//
// MessageId: AFRES_AFARCHIVE_PURGE_FAILED
//
// MessageText:
//
// File Copy Purge job has failed.%0
//
#define AFRES_AFARCHIVE_PURGE_FAILED     ((DWORD)0xC0070016L)

//
// MessageId: AFRES_AFARCHIVE_CATALOGRESYNC_COMPLETE
//
// MessageText:
//
// File Copy catalog resync successfully completed.%0
//
#define AFRES_AFARCHIVE_CATALOGRESYNC_COMPLETE ((DWORD)0x40070017L)

//
// MessageId: AFRES_AFARCHIVE_CATALOGRESYNC_FAILURE
//
// MessageText:
//
// File Copy catalog resync failed.%0
//
#define AFRES_AFARCHIVE_CATALOGRESYNC_FAILURE ((DWORD)0x40070018L)

//
// MessageId: AFRES_AFARCHIVE_CHANGING_BACK2_OLDDEST
//
// MessageText:
//
// %1!s! will use the existing catalogs as the user changed back to the old %2!s! destination.%0
//
#define AFRES_AFARCHIVE_CHANGING_BACK2_OLDDEST ((DWORD)0x40070019L)

//
// MessageId: AFRES_AFARCHIVE_AMAZON_CLK_SKEW
//
// MessageText:
//
// Clock skew detected on machine. Please adjust the system time or timezone.%0
//
#define AFRES_AFARCHIVE_AMAZON_CLK_SKEW  ((DWORD)0xC007001AL)

//
// MessageId: AFRES_AFARCHIVE_GEN_CCIERRORCODE
//
// MessageText:
//
// An internal error has been encountered that prevent %1!s! operation to continue, please contact arcserve support if the problem persist.%0
//
#define AFRES_AFARCHIVE_GEN_CCIERRORCODE ((DWORD)0xC007001BL)

//
// MessageId: AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_START
//
// MessageText:
//
// File Copy Catalog Resync job for alternate location has started.%0
//
#define AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_START ((DWORD)0x4007001CL)

//
// MessageId: AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_COMPLETE
//
// MessageText:
//
// File Copy Catalog Resync job for alternate location has finished.%0
//
#define AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_COMPLETE ((DWORD)0x4007001DL)

//
// MessageId: AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_FAILED
//
// MessageText:
//
// File Copy Catalog Resync job for alternate location has failed.%0
//
#define AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_FAILED ((DWORD)0xC007001EL)

//
// MessageId: AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_CANCELLED
//
// MessageText:
//
// File Copy Catalog Resync job for alternate location has been canceled.%0
//
#define AFRES_AFARCHIVE_ALTLOC_CATALOGRESYNC_CANCELLED ((DWORD)0x8007001FL)

//
// MessageId: AFRES_AFARCHIVE_SKIP_ARCHIVE_DESTCHANGED
//
// MessageText:
//
// %1!s! destination change has been detected, %2!s! job will be skipped.%0
//
#define AFRES_AFARCHIVE_SKIP_ARCHIVE_DESTCHANGED ((DWORD)0x80070020L)

//
// MessageId: AFRES_AFARCHIVE_SKIP_ARCHIVE_DESTNOTINIT
//
// MessageText:
//
// %1!s! destination is corrupted or has not been initialized, %2!s! job will be failed.%0
//
#define AFRES_AFARCHIVE_SKIP_ARCHIVE_DESTNOTINIT ((DWORD)0xC0070021L)

//
// MessageId: AFRES_AFARCHIVE_ARCHIVE_DESTINATION_NOT_INIT
//
// MessageText:
//
// The destination is not a valid %1!s! destination.%0
//
#define AFRES_AFARCHIVE_ARCHIVE_DESTINATION_NOT_INIT ((DWORD)0x80070022L)

//
// MessageId: AFRES_AFARCHIVE_PURGE_JOB_SKIPPED
//
// MessageText:
//
// File Copy Purge job has been skipped - destination is not initialized.%0
//
#define AFRES_AFARCHIVE_PURGE_JOB_SKIPPED ((DWORD)0x40070023L)

//
// MessageId: AFRES_AFARCHIVE_DELETE_FAILED_COUNT
//
// MessageText:
//
// Total files not deleted for File Archive job on source : %1!d!%0
//
#define AFRES_AFARCHIVE_DELETE_FAILED_COUNT ((DWORD)0x80070024L)

//
// MessageId: AFRES_AFARCHIVE_NODISKSPACE_DESTINATION
//
// MessageText:
//
// There is not enough space on the destination device.%0
//
#define AFRES_AFARCHIVE_NODISKSPACE_DESTINATION ((DWORD)0xC0070025L)

//
// MessageId: AFRES_AFARCHIVE_NWSHARE_1219ERROR
//
// MessageText:
//
// A connection is active to the same network resource with different credential.%0
//
#define AFRES_AFARCHIVE_NWSHARE_1219ERROR ((DWORD)0xC0070026L)

//
// MessageId: AFRES_AFARCHIVE_NWSHARE_BAD_USERNAME
//
// MessageText:
//
// The specified network resource username is invalid.%0
//
#define AFRES_AFARCHIVE_NWSHARE_BAD_USERNAME ((DWORD)0xC0070027L)

//
// MessageId: AFRES_AFARCHIVE_NWSHARE_BAD_PASSWORD
//
// MessageText:
//
// The specified network resource password is invalid.%0
//
#define AFRES_AFARCHIVE_NWSHARE_BAD_PASSWORD ((DWORD)0xC0070028L)

//
// MessageId: AFRES_AFARCHIVE_NWSHARE_NETWORKERROR
//
// MessageText:
//
// The network is unavailable.%0
//
#define AFRES_AFARCHIVE_NWSHARE_NETWORKERROR ((DWORD)0xC0070029L)

//
// MessageId: AFRES_AFARCHIVE_POLICY_SOURCE_DOESNOT_EXIST
//
// MessageText:
//
// Source %1!s! selected in policy does not exist. This source might not be FileCopied in current or subsequent jobs.%0
//
#define AFRES_AFARCHIVE_POLICY_SOURCE_DOESNOT_EXIST ((DWORD)0x4007002AL)

//
// MessageId: AFRES_AFARCHIVE_BACKUP_STATISTICS_FILECOPY_DEL
//
// MessageText:
//
// Total files copied is : %1!d!. Total Disk Space saved after File Archive is %2!s!.%0
//
#define AFRES_AFARCHIVE_BACKUP_STATISTICS_FILECOPY_DEL ((DWORD)0x4007002BL)

//
// MessageId: AFRES_AFARCHIVE_BACKUP_STATISTICS_FILECOPY_COUNT
//
// MessageText:
//
// Total files copied :%1!d!.%0
//
#define AFRES_AFARCHIVE_BACKUP_STATISTICS_FILECOPY_COUNT ((DWORD)0x4007002CL)

//
// MessageId: AFRES_AFARCHIVE_SKIP_FILE_COPY
//
// MessageText:
//
// Failed to copy file: %1!s! error = %2!d!, skipping this file.%0
//
#define AFRES_AFARCHIVE_SKIP_FILE_COPY   ((DWORD)0x4007002DL)

//
// MessageId: AFRES_AFARCHIVE_STUBFILE_HEADING
//
// MessageText:
//
// This is a stub file created by ^AU_ProductName_UDP_SHORT^ (File Archive).%0
//
#define AFRES_AFARCHIVE_STUBFILE_HEADING ((DWORD)0x4007002EL)

//
// MessageId: AFRES_AFARCHIVE_STUBFILE_FILENAME_MSG
//
// MessageText:
//
// The File [%s] is moved to the following File Archive destination on %s.%0
//
#define AFRES_AFARCHIVE_STUBFILE_FILENAME_MSG ((DWORD)0x4007002FL)

//
// MessageId: AFRES_AFARCHIVE_STUBFILE_DEST_PATH
//
// MessageText:
//
// Destination Path : %0
//
#define AFRES_AFARCHIVE_STUBFILE_DEST_PATH ((DWORD)0x40070030L)

//
// MessageId: AFRES_AFARCHIVE_STUBFILE_CLOUDDEST_URL
//
// MessageText:
//
// Cloud Dest URL   : %0
//
#define AFRES_AFARCHIVE_STUBFILE_CLOUDDEST_URL ((DWORD)0x40070031L)

//
// MessageId: AFRES_AFARCHIVE_STUBFILE_CLOUDDEST_CONTAINER
//
// MessageText:
//
// Cloud Container   : %0
//
#define AFRES_AFARCHIVE_STUBFILE_CLOUDDEST_CONTAINER ((DWORD)0x40070032L)

//
// MessageId: AFRES_AFARCHIVE_AMAZONDEST_DETAILS
//
// MessageText:
//
// %1!s! destination for this job is Amazon Cloud and the details are URL:: %2!s! and Bucket:: %3!s!%0
//
#define AFRES_AFARCHIVE_AMAZONDEST_DETAILS ((DWORD)0x40070033L)

//
// MessageId: AFRES_AFARCHIVE_AZUREDEST_DETAILS
//
// MessageText:
//
// %1!s! destination for this job is Azure Cloud and the details are URL:: %2!s! and Container:: %3!s!%0
//
#define AFRES_AFARCHIVE_AZUREDEST_DETAILS ((DWORD)0x40070034L)

//
// MessageId: AFRES_AFARCHIVE_WRITE_FAILED
//
// MessageText:
//
// Cannot write to the specified device due to problem with network or service provider%0
//
#define AFRES_AFARCHIVE_WRITE_FAILED     ((DWORD)0xC0070035L)

//
// MessageId: AFRES_AFARCHIVE_READ_FAILED
//
// MessageText:
//
// Cannot read from the specified device due to problem with network or service provider.%0
//
#define AFRES_AFARCHIVE_READ_FAILED      ((DWORD)0xC0070036L)

//
// MessageId: AFRES_AFARCHIVE_NWSHARE_WRITE_PRIV_MISSING
//
// MessageText:
//
// The %1!s! destination is not writable.%0
//
#define AFRES_AFARCHIVE_NWSHARE_WRITE_PRIV_MISSING ((DWORD)0xC0070037L)

//
// MessageId: AFRES_AFARCHIVE_EUCALYPTUS_DETAILS
//
// MessageText:
//
// %1!s! destination for this job is Eucalyptus-Walrus Cloud and the details are URL:: %2!s! and Bucket:: %3!s!.%0
//
#define AFRES_AFARCHIVE_EUCALYPTUS_DETAILS ((DWORD)0x40070038L)

//
// MessageId: AFRES_AFARCHIVE_DEST_ENCR_SETTINGS_MISMATCH
//
// MessageText:
//
// The %1!s! Encryption settings on the destination and the one in restore job don't match.%0
//
#define AFRES_AFARCHIVE_DEST_ENCR_SETTINGS_MISMATCH ((DWORD)0xC0070039L)

//
// MessageId: AFRES_AFARCHIVE_DEST_ENCR_PASSWORD_MISMATCH
//
// MessageText:
//
// Incorrect %1!s! destination encryption password.%0
//
#define AFRES_AFARCHIVE_DEST_ENCR_PASSWORD_MISMATCH ((DWORD)0xC007003AL)

//
// MessageId: AFRES_AFARCHIVE_DEST_ENCR_GEN_CCIERROR
//
// MessageText:
//
// The File Copy destination encryption settings validation failed with CCI error [%d]%0
//
#define AFRES_AFARCHIVE_DEST_ENCR_GEN_CCIERROR ((DWORD)0xC007003BL)

//
// MessageId: AFRES_AFARCHIVE_FILE_COPY_SIZE
//
// MessageText:
//
// Total data size written to %1!s! destination is %2!s!%0
//
#define AFRES_AFARCHIVE_FILE_COPY_SIZE   ((DWORD)0x4007003CL)

//
// MessageId: AFRES_AFARCHIVE_DEST_NO_LICENSE_LIC
//
// MessageText:
//
// D2D2D (File Copy to a local or network drive) feature license is not available for the current configured destination, skipping the file copy job.%0
//
#define AFRES_AFARCHIVE_DEST_NO_LICENSE_LIC ((DWORD)0xC007003DL)

//
// MessageId: AFRES_AFARCHIVE_DEST_NO_ENCRYPTION_LIC
//
// MessageText:
//
// Encryption feature license is not available, skipping the file copy job.%0
//
#define AFRES_AFARCHIVE_DEST_NO_ENCRYPTION_LIC ((DWORD)0xC007003EL)

//
// MessageId: AFRES_AFARCHIVE_RESTORE_FS_SUMMARY
//
// MessageText:
//
// The total download size is %1!s! and the size of %2!d! files restored to disk is %3!s! The elapsed time for the restore job is %4!s!, with an average speed (throughput) of %5!s!/min.%0
//
#define AFRES_AFARCHIVE_RESTORE_FS_SUMMARY ((DWORD)0x4007003FL)

//
// MessageId: AFRES_AFARCHIVE_RESTORE_SKIP
//
// MessageText:
//
// %1!lu! Files Skipped.%0
//
#define AFRES_AFARCHIVE_RESTORE_SKIP     ((DWORD)0x40070040L)

//
// MessageId: AFRES_AFARCHIVE_INVALID_DESTINATION_PATH
//
// MessageText:
//
// Invalid File Copy destination path specified.%0
//
#define AFRES_AFARCHIVE_INVALID_DESTINATION_PATH ((DWORD)0xC0070041L)

//
// MessageId: AFRES_AFARCHIVE_MAKEUP_JOB_MESSAGE
//
// MessageText:
//
// A makeup job will be created for this job because the current %1!s! job was not completed. This makeup job will be run after 30 minutes.%0
//
#define AFRES_AFARCHIVE_MAKEUP_JOB_MESSAGE ((DWORD)0x80070042L)

//
// MessageId: AFRES_AFARCHIVE_SKIP_FILECOUNT
//
// MessageText:
//
// Total files skipped : %1!d! %0
//
#define AFRES_AFARCHIVE_SKIP_FILECOUNT   ((DWORD)0x40070043L)

//
// MessageId: AFRES_AFARCHIVE_WALRUS_SKIP_FILE_WITH_SPECIAL_CHAR
//
// MessageText:
//
// Failed to create file %1!s! at specified destination. Please ensure destination can support all characters in the file/folder name.%0
//
#define AFRES_AFARCHIVE_WALRUS_SKIP_FILE_WITH_SPECIAL_CHAR ((DWORD)0x40070044L)

//
// MessageId: AFRES_AFARCHIVE_WALRUS_SKIP_FILE_LONGPATH
//
// MessageText:
//
// Skipping file %1!s!. File path exceeds the maximum length %2!d! supported by the destination device.%0
//
#define AFRES_AFARCHIVE_WALRUS_SKIP_FILE_LONGPATH ((DWORD)0x40070045L)

//
// MessageId: AFRES_AFARCHIVE_DISK_FULL
//
// MessageText:
//
// Failed to copy file %1!s!. There is not enough available space at the specified %2!s! destination.%0
//
#define AFRES_AFARCHIVE_DISK_FULL        ((DWORD)0xC0070046L)

//
// MessageId: AFRES_AFARCHIVE_NETWORK_ERROR
//
// MessageText:
//
// Failed to copy file %1!s!. Make sure you are properly connected to the network and try again.%0
//
#define AFRES_AFARCHIVE_NETWORK_ERROR    ((DWORD)0xC0070047L)

//
// MessageId: AFRES_AFARCHIVE_FUZITSU_DEST_DETAILS
//
// MessageText:
//
// %1!s! destination for this job is Fujitsu Cloud (Windows Azure) and the details are URL:: %2!s! and Container:: %3!s!.%0
//
#define AFRES_AFARCHIVE_FUZITSU_DEST_DETAILS ((DWORD)0x40070048L)

//
// MessageId: AFRES_AFARCHIVE_HTTP_ERRORCODE
//
// MessageText:
//
// An internal error has been encountered with Cloud connection that prevent %1!s! operation to continue, please contact arcserve support if the problem persist.%0
//
#define AFRES_AFARCHIVE_HTTP_ERRORCODE   ((DWORD)0xC0070049L)

//
// MessageId: AFRES_AFARCHIVE_COMPRESSRATIO
//
// MessageText:
//
// The total processed data size on the source disks is %1!s! and the total data size written is %2!s!. The space saved from compression is %3!d!%4!c!%5!02d!%%.%0
//
#define AFRES_AFARCHIVE_COMPRESSRATIO    ((DWORD)0x4007004AL)

//
// MessageId: AFRES_AFCOMM_SESS_LOCKED_BY_OTHERS_1
//
// MessageText:
//
// Session resources are locked [%1!s!].%0
//
#define AFRES_AFCOMM_SESS_LOCKED_BY_OTHERS_1 ((DWORD)0xC007004BL)

//
// MessageId: AFRES_OFFCAT_DISABLE_FILE_COPY_FOR_CHECK_VOLUME_ERROR_1
//
// MessageText:
//
// Failed to check volumes %1!s! for machine %2!s! to determine if the volumes are a file copy source. File copy will be disabled.%0
//
#define AFRES_OFFCAT_DISABLE_FILE_COPY_FOR_CHECK_VOLUME_ERROR_1 ((DWORD)0x8007004CL)

//
// MessageId: AFRES_OFFCAT_SKIP_REFS_VOLUME_FOR_FILE_COPY_1
//
// MessageText:
//
// Volume %1!s! is ReFS volume in machine %2!s!, %3!s! will skip this volume%0
//
#define AFRES_OFFCAT_SKIP_REFS_VOLUME_FOR_FILE_COPY_1 ((DWORD)0x8007004DL)

//
// MessageId: AFRES_OFFCAT_SKIP_DEDUPE_VOLUME_FOR_FILE_COPY_1
//
// MessageText:
//
// Volume %1!s! is Deduplication-Enabled volume in machine %2!s!, %3!s! will skip this volume%0
//
#define AFRES_OFFCAT_SKIP_DEDUPE_VOLUME_FOR_FILE_COPY_1 ((DWORD)0x8007004EL)

//
// MessageId: AFRES_OFFCAT_SKIP_UNSUPPORTED_VOLUME_FOR_FILE_COPY_1
//
// MessageText:
//
// %1!s! is not supported for the volume %2!s! in machine %3!s!.%0
//
#define AFRES_OFFCAT_SKIP_UNSUPPORTED_VOLUME_FOR_FILE_COPY_1 ((DWORD)0x8007004FL)

//
// MessageId: AFRES_AFARCHIVE_FCDELETESOURCE_STARTED
//
// MessageText:
//
// File Copy Delete job has started.%0
//
#define AFRES_AFARCHIVE_FCDELETESOURCE_STARTED ((DWORD)0x40070050L)

//
// MessageId: AFRES_AFARCHIVE_FCDELETESOURCE_END
//
// MessageText:
//
// File Copy Delete job has ended.%0
//
#define AFRES_AFARCHIVE_FCDELETESOURCE_END ((DWORD)0x40070051L)

//
// MessageId: AFRES_AFARCHIVE_FCDELETESOURCE_STATS
//
// MessageText:
//
// Total Files Archived: %1!d!. TotalSize saved on source disk: %2!s!.%0
//
#define AFRES_AFARCHIVE_FCDELETESOURCE_STATS ((DWORD)0x40070052L)

//
// MessageId: AFRES_AFARCHIVE_FCDELETESOURCE_FAILED
//
// MessageText:
//
// File Copy Delete job has failed.%0
//
#define AFRES_AFARCHIVE_FCDELETESOURCE_FAILED ((DWORD)0xC0070053L)

//
// MessageId: AFRES_AFARCHIVE_FCDELETESOURCE_CANCELLED
//
// MessageText:
//
// File Copy Delete job has cancelled.%0
//
#define AFRES_AFARCHIVE_FCDELETESOURCE_CANCELLED ((DWORD)0x80070054L)

//
// MessageId: AFRES_AFARCHIVE_RESTORE_VOL_NOT_FOUND
//
// MessageText:
//
// The Drive [%1!s!] does not exist.%0
//
#define AFRES_AFARCHIVE_RESTORE_VOL_NOT_FOUND ((DWORD)0xC0070055L)

//
// MessageId: AFRES_AFARCHIVE_START_RESTORE
//
// MessageText:
//
// Start Restore Operation.%0
//
#define AFRES_AFARCHIVE_START_RESTORE    ((DWORD)0x40070056L)

//
// MessageId: AFRES_AFARCHIVE_CHANGE_CRYPTO_FAILED
//
// MessageText:
//
// The encryption password does not match with original destination password for the node[%1!s!]. Either get the original password or use new destination.%0
//
#define AFRES_AFARCHIVE_CHANGE_CRYPTO_FAILED ((DWORD)0xC0070057L)

//
// MessageId: AFRES_AFARCHIVE_ENABLE_CRYPTO_FAILED
//
// MessageText:
//
// Encryption mismatch with the %1!s! destination. To enable encryption settings, specify a new destination for the node[%2!s!].%0
//
#define AFRES_AFARCHIVE_ENABLE_CRYPTO_FAILED ((DWORD)0xC0070058L)

//
// MessageId: AFRES_AFARCHIVE_DISABLE_CRYPTO_FAILED
//
// MessageText:
//
// Encryption mismatch with the %1!s! destination. To disable encryption settings, specify a new destination for the node[%2!s!].%0
//
#define AFRES_AFARCHIVE_DISABLE_CRYPTO_FAILED ((DWORD)0xC0070059L)

//
// MessageId: AFRES_AFARCHIVE_PRINT_LOG_FILENAME
//
// MessageText:
//
// Please refer the log file [%1!s!] to know the list of files that were skipped from copying.%0
//
#define AFRES_AFARCHIVE_PRINT_LOG_FILENAME ((DWORD)0x4007005AL)

//
// MessageId: AFRES_AFARCHIVE_TOO_MANY_BUCKETS
//
// MessageText:
//
// Failed to create a new bucket. Reason: Too many buckets in cloud. Please refer to amazon plugin logs for more details.%0
//
#define AFRES_AFARCHIVE_TOO_MANY_BUCKETS ((DWORD)0xC007005BL)

//
// MessageId: AFRES_AFARCHIVE_NO_NEW_FILES
//
// MessageText:
//
// No new or modified files available for %1!s! backup.%0
//
#define AFRES_AFARCHIVE_NO_NEW_FILES     ((DWORD)0xC007005CL)

//
// MessageId: AFRES_AFARCHIVE_NO_VOL_IN_BACKUP
//
// MessageText:
//
// The selected source folders for %1!s! are not part of the Backup settings.%0
//
#define AFRES_AFARCHIVE_NO_VOL_IN_BACKUP ((DWORD)0x8007005DL)

//
// MessageId: AFRES_AFARCHIVE_RESTORE_INTENDED_SKIP
//
// MessageText:
//
// Skip option is selected as conflict resolution option for existing files. %1!lu! files are skipped with respect to this option and %2!lu! files are skipped for other reasons.%0
//
#define AFRES_AFARCHIVE_RESTORE_INTENDED_SKIP ((DWORD)0x4007005EL)

//
// MessageId: AFRES_AFARCHIVE_ACCESS_DENIED
//
// MessageText:
//
// Authentication Failure. The provided user credentials failed validation for the requested service.%0
//
#define AFRES_AFARCHIVE_ACCESS_DENIED    ((DWORD)0xC007005FL)

//
// MessageId: AFRES_AFARCHIVE_FLIST_GEN_FAILED
//
// MessageText:
//
// Failed to generate the incremental blocks for the volume[%1!s!].%0
//
#define AFRES_AFARCHIVE_FLIST_GEN_FAILED ((DWORD)0x80070060L)

//
// MessageId: AFRES_AFARCHIVE_SKIP_MOUNTVOL
//
// MessageText:
//
// Encountered a mount-point folder[%1!s!]. This folder will be skipped from backup.%0
//
#define AFRES_AFARCHIVE_SKIP_MOUNTVOL    ((DWORD)0x80070061L)

//
// MessageId: AFRES_AFARCHIVE_FC_NW_THROUGHPUT
//
// MessageText:
//
// The amount of data actually transferred to destination was %1!s!, and the average network throughput was %2!s!/Sec.%0
//
#define AFRES_AFARCHIVE_FC_NW_THROUGHPUT ((DWORD)0x40070062L)

//
// MessageId: AFRES_AFARCHIVE_STUBFILE_DEFAULTMSG
//
// MessageText:
//
// Please contact your IT department to restore this file.%0
//
#define AFRES_AFARCHIVE_STUBFILE_DEFAULTMSG ((DWORD)0x40070063L)

//
// MessageId: AFRES_AFARCHIVE_FILECOPY
//
// MessageText:
//
// File Copy%0
//
#define AFRES_AFARCHIVE_FILECOPY         ((DWORD)0x40070064L)

//
// MessageId: AFRES_AFARCHIVE_FILEARCHIVE
//
// MessageText:
//
// File Archive%0
//
#define AFRES_AFARCHIVE_FILEARCHIVE      ((DWORD)0x40070065L)

//
// MessageId: AFRES_AFARCHIVE_SKIP_FILE_LONG_FILENAME
//
// MessageText:
//
// Skipping file %1!s!. File name length exceeds the maximum length %2!d! supported by the destination device.%0
//
#define AFRES_AFARCHIVE_SKIP_FILE_LONG_FILENAME ((DWORD)0x40070066L)

//
// MessageId: AFRES_AFARCHIVE_LOG_RPS_DS_SESS
//
// MessageText:
//
// Will perform %1!s! from RPS [%2!s!] Datastore [%3!s!] SessionNo [%4!d!]%0
//
#define AFRES_AFARCHIVE_LOG_RPS_DS_SESS  ((DWORD)0x40070067L)

//
// MessageId: AFRES_AFARCHIVE_LOG_SRC_FOLDER
//
// MessageText:
//
// Source Folder: %1!s!
//
#define AFRES_AFARCHIVE_LOG_SRC_FOLDER   ((DWORD)0x40070068L)

//
// MessageId: AFRES_AFARCHIVE_BUCKET
//
// MessageText:
//
// Bucket%0
//
#define AFRES_AFARCHIVE_BUCKET           ((DWORD)0x40070069L)

//
// MessageId: AFRES_AFARCHIVE_CONTAINER
//
// MessageText:
//
// Container%0
//
#define AFRES_AFARCHIVE_CONTAINER        ((DWORD)0x4007006AL)

//
// MessageId: AFRES_AFARCHIVE_INVALID_BUCKET_NAME
//
// MessageText:
//
// Invalid %1!s! name. Please check cloud vendor rules for naming a %2!s!.%0
//
#define AFRES_AFARCHIVE_INVALID_BUCKET_NAME ((DWORD)0xC007006BL)

//
// MessageId: AFRES_AFARCHIVE_SKIP_SYSTEM_FOLDERS_FILES
//
// MessageText:
//
// All system folders/files will be skipped%0
//
#define AFRES_AFARCHIVE_SKIP_SYSTEM_FOLDERS_FILES ((DWORD)0x4007006CL)

 // The following are the message definitions for Backup
//
// MessageId: AFRES_AFBKND_JOB_CRASHED
//
// MessageText:
//
// Job crashed.%0
//
#define AFRES_AFBKND_JOB_CRASHED         ((DWORD)0xC0080001L)

//
// MessageId: AFRES_AFBKND_SYSTEM_SHUTDOWN
//
// MessageText:
//
// The system is shutting down.%0
//
#define AFRES_AFBKND_SYSTEM_SHUTDOWN     ((DWORD)0x80080002L)

//
// MessageId: AFRES_AFBKND_ANOTHER_SAME_TYPE_JOB_RUNNING
//
// MessageText:
//
// Current job will be canceled because another job with the same type is currently running.%0
//
#define AFRES_AFBKND_ANOTHER_SAME_TYPE_JOB_RUNNING ((DWORD)0xC0080003L)

//
// MessageId: AFRES_AFBKDL_PHASE_TAKING_SNAPSHOT
//
// MessageText:
//
// Taking snapshot ...%0
//
#define AFRES_AFBKDL_PHASE_TAKING_SNAPSHOT ((DWORD)0x40080004L)

//
// MessageId: AFRES_AFBKDL_PHASE_CREATING_VIRTUAL_DISKS
//
// MessageText:
//
// Creating virtual disks ...%0
//
#define AFRES_AFBKDL_PHASE_CREATING_VIRTUAL_DISKS ((DWORD)0x40080005L)

//
// MessageId: AFRES_AFBKDL_PHASE_REPLICATIING_VOLUMES
//
// MessageText:
//
// Backing up volumes ...%0
//
#define AFRES_AFBKDL_PHASE_REPLICATIING_VOLUMES ((DWORD)0x40080006L)

//
// MessageId: AFRES_AFBKDL_PHASE_DELETING_SNAPSHOT
//
// MessageText:
//
// Deleting snapshot ...%0
//
#define AFRES_AFBKDL_PHASE_DELETING_SNAPSHOT ((DWORD)0x40080007L)

//
// MessageId: AFRES_AFBKDL_NEW_SESSION_NUMBER
//
// MessageText:
//
// Created new session %1!d!.%0
//
#define AFRES_AFBKDL_NEW_SESSION_NUMBER  ((DWORD)0x40080008L)

//
// MessageId: AFRES_AFBKDL_JOB_FAILED
//
// MessageText:
//
// Backup job failed.%0
//
#define AFRES_AFBKDL_JOB_FAILED          ((DWORD)0xC0080009L)

//
// MessageId: AFRES_AFBKDL_JOB_FINISHED
//
// MessageText:
//
// Backup job completed successfully.%0
//
#define AFRES_AFBKDL_JOB_FINISHED        ((DWORD)0x4008000AL)

//
// MessageId: AFRES_AFBKDL_BEGIN_VOLUME
//
// MessageText:
//
// Beginning backup of volume %1!s! ...%0
//
#define AFRES_AFBKDL_BEGIN_VOLUME        ((DWORD)0x4008000BL)

//
// MessageId: AFRES_AFBKDL_END_VOLUME
//
// MessageText:
//
// Backup of volume %1!s! completed successfully.%0
//
#define AFRES_AFBKDL_END_VOLUME          ((DWORD)0x4008000CL)

//
// MessageId: AFRES_AFBKDL_BACKUP_STARTED
//
// MessageText:
//
// Backup of node %1!s! started.%0
//
#define AFRES_AFBKDL_BACKUP_STARTED      ((DWORD)0x4008000DL)

//
// MessageId: AFRES_AFBKDL_REPLICATE_VOLUMES
//
// MessageText:
//
// Will perform backup of %1!d! volume(s) - %2!s!.%0
//
#define AFRES_AFBKDL_REPLICATE_VOLUMES   ((DWORD)0x4008000EL)

//
// MessageId: AFRES_AFBKDL_DUMP_METADATA
//
// MessageText:
//
// Dump volume %1!s! metadata.%0
//
#define AFRES_AFBKDL_DUMP_METADATA       ((DWORD)0x4008000FL)

//
// MessageId: AFRES_AFBKDL_DUMP_METADATA_FAIL
//
// MessageText:
//
// Dump volume %1!s! metadata failed.%0
//
#define AFRES_AFBKDL_DUMP_METADATA_FAIL  ((DWORD)0x80080010L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_FAIL
//
// MessageText:
//
// Unable to execute command. (EC=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_FAIL     ((DWORD)0xC0080011L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_GENERIC_ERR_PROCESS_NOT_ABORTED_DENIED
//
// MessageText:
//
// Job Aborted.  Generic Task process could not be stopped.(EC=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_GENERIC_ERR_PROCESS_NOT_ABORTED_DENIED ((DWORD)0xC0080012L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_GENERIC_ERR_JOB_AND_PROCESS_ABORTED
//
// MessageText:
//
// Job Aborted.  Generic Task process successfully stopped.%0
//
#define AFRES_AFBKDL_PREPOSTCMD_GENERIC_ERR_JOB_AND_PROCESS_ABORTED ((DWORD)0x80080013L)

//
// MessageId: AFRES_AFBKDL_JOB_NAME
//
// MessageText:
//
// Job name: %1!s!.%0
//
#define AFRES_AFBKDL_JOB_NAME            ((DWORD)0x40080014L)

//
// MessageId: AFRES_AFBKDL_JOB_METHOD_FULL
//
// MessageText:
//
// Full backup%0
//
#define AFRES_AFBKDL_JOB_METHOD_FULL     ((DWORD)0x40080015L)

//
// MessageId: AFRES_AFBKDL_JOB_METHOD_INCR
//
// MessageText:
//
// Incremental backup%0
//
#define AFRES_AFBKDL_JOB_METHOD_INCR     ((DWORD)0x40080016L)

//
// MessageId: AFRES_AFBKDL_JOB_METHOD_RESYNC
//
// MessageText:
//
// Verify backup%0
//
#define AFRES_AFBKDL_JOB_METHOD_RESYNC   ((DWORD)0x40080017L)

//
// MessageId: AFRES_AFBKDL_COMPRESSION_LEVEL_NONE
//
// MessageText:
//
// Compression level is none%0
//
#define AFRES_AFBKDL_COMPRESSION_LEVEL_NONE ((DWORD)0x40080018L)

//
// MessageId: AFRES_AFBKDL_COMPRESSION_LEVEL_STANDARD
//
// MessageText:
//
// Compression level is standard%0
//
#define AFRES_AFBKDL_COMPRESSION_LEVEL_STANDARD ((DWORD)0x40080019L)

//
// MessageId: AFRES_AFBKDL_COMPRESSION_LEVEL_MAX
//
// MessageText:
//
// Compression level is maximum%0
//
#define AFRES_AFBKDL_COMPRESSION_LEVEL_MAX ((DWORD)0x4008001AL)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE
//
// MessageText:
//
// Converting to a full backup because virtual disk format has been changed.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE       ((DWORD)0x8008001BL)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_FIRST_JOB
//
// MessageText:
//
// Converting the first job to a full backup.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_FIRST_JOB ((DWORD)0x8008001CL)

//
// MessageId: AFRES_AFBKDL_DEST_VOLUME_SKIPPED
//
// MessageText:
//
// Local destination volume %1!s! is skipped.%0
//
#define AFRES_AFBKDL_DEST_VOLUME_SKIPPED ((DWORD)0x8008001DL)

//
// MessageId: AFRES_AFBKDL_RAID_VOLUME_SKIPPED
//
// MessageText:
//
// RAID 5 volume %1!s! is skipped.%0
//
#define AFRES_AFBKDL_RAID_VOLUME_SKIPPED ((DWORD)0x8008001EL)

//
// MessageId: AFRES_AFBKDL_NON_NTFS_VOLUME_SKIPPED
//
// MessageText:
//
// Non NTFS volume %1!s! is skipped, the file system is %2!s!. If full volume encryption (for example, Window Bitlocker) is enabled on this volume, unlock / decrypt the volume and try to run backup again.%0
//
#define AFRES_AFBKDL_NON_NTFS_VOLUME_SKIPPED ((DWORD)0x8008001FL)

//
// MessageId: AFRES_AFBKDL_VHD_VOLUME_SKIPPED
//
// MessageText:
//
// Volume %1!s! hosted in VHD is skipped.%0
//
#define AFRES_AFBKDL_VHD_VOLUME_SKIPPED  ((DWORD)0x80080020L)

//
// MessageId: AFRES_AFBKDL_FAIL_LOCATE_DEST
//
// MessageText:
//
// Failed to locate backup destination. (Path=[%1!s!], Username=[%2!s!], EC=[%3!d!: %4!s!])%0
//
#define AFRES_AFBKDL_FAIL_LOCATE_DEST    ((DWORD)0xC0080021L)

//
// MessageId: AFRES_AFBKDL_FAIL_WRITE_DISK
//
// MessageText:
//
// Failed to write to destination. System error=[%1!s!].%0
//
#define AFRES_AFBKDL_FAIL_WRITE_DISK     ((DWORD)0xC0080022L)

//
// MessageId: AFRES_AFBKDL_VOLUME_EST_DATA_SIZE
//
// MessageText:
//
// Estimated backup data size. Volume=%1!s!, size=%2!s!.%0
//
#define AFRES_AFBKDL_VOLUME_EST_DATA_SIZE ((DWORD)0x40080023L)

//
// MessageId: AFRES_AFBKDL_VOLUME_ELAPSED_TIME
//
// MessageText:
//
// Elapsed time. Volume=%1!s!, time=%2!s!.%0
//
#define AFRES_AFBKDL_VOLUME_ELAPSED_TIME ((DWORD)0x40080024L)

//
// MessageId: AFRES_AFBKDL_VOLUME_DEST_PATH
//
// MessageText:
//
// Backup destination is %1!s!.%0
//
#define AFRES_AFBKDL_VOLUME_DEST_PATH    ((DWORD)0x40080025L)

//
// MessageId: AFRES_AFBKDL_TOTAL_THROUGHPUT
//
// MessageText:
//
// Backup job processed total of %1!s! of source data in %2!s!, and the backup job throughput was %3!s!/Min.%0
//
#define AFRES_AFBKDL_TOTAL_THROUGHPUT    ((DWORD)0x40080026L)

//
// MessageId: AFRES_AFBKDL_JOB_CANCELLED
//
// MessageText:
//
// Backup job canceled.%0
//
#define AFRES_AFBKDL_JOB_CANCELLED       ((DWORD)0x80080027L)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_BMR
//
// MessageText:
//
// Converting to a verify backup because system was just restored from Bare Metal Recovery.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_BMR   ((DWORD)0x40080028L)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_FRESHINSTALL
//
// MessageText:
//
// Converting the first job after installation / upgrade to a verify backup.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_FRESHINSTALL ((DWORD)0x40080029L)

//
// MessageId: AFRES_AFBKDL_DR_INFO_OK
//
// MessageText:
//
// Collect Bare Metal Recovery information finished.%0
//
#define AFRES_AFBKDL_DR_INFO_OK          ((DWORD)0x4008002AL)

//
// MessageId: AFRES_AFBKDL_DR_INFO_FAIL
//
// MessageText:
//
// Collect Bare Metal Recovery information failed, this recovery point cannot be used to do Bare Metal Recovery.%0
//
#define AFRES_AFBKDL_DR_INFO_FAIL        ((DWORD)0x8008002BL)

//
// MessageId: AFRES_AFBKDL_FOUND_BAD_CLUSTER
//
// MessageText:
//
// Found data CRC error on volume %1!s! within offset %2!I64d! and length %3!d! bytes.%0
//
#define AFRES_AFBKDL_FOUND_BAD_CLUSTER   ((DWORD)0x8008002CL)

//
// MessageId: AFRES_AFBKDL_ENTER_LOG_PURGING
//
// MessageText:
//
// Started transaction log pruning.%0
//
#define AFRES_AFBKDL_ENTER_LOG_PURGING   ((DWORD)0x4008002DL)

//
// MessageId: AFRES_AFBKDL_PURGE_SQL_LOG
//
// MessageText:
//
// Purged Microsoft SQL Server log for database %1!s!\%2!s!.%0
//
#define AFRES_AFBKDL_PURGE_SQL_LOG       ((DWORD)0x4008002EL)

//
// MessageId: AFRES_AFBKDL_PURGE_EXCH_LOG
//
// MessageText:
//
// Purged Microsoft Exchange log under %1!s! (%2!s!).%0
//
#define AFRES_AFBKDL_PURGE_EXCH_LOG      ((DWORD)0x4008002FL)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_FAILJOB_ON_EXIT_CODE
//
// MessageText:
//
// Job Pre-Command has exit code %1!d!.  Abort the job.%0
//
#define AFRES_AFBKDL_PREPOSTCMD_FAILJOB_ON_EXIT_CODE ((DWORD)0x40080030L)

//
// MessageId: AFRES_AFBKDL_DELAY_TO_PURGE_SESSION
//
// MessageText:
//
// Delay to merge session because virtual disks are being read for catalog generation of Exchange Granular Restore.%0
//
#define AFRES_AFBKDL_DELAY_TO_PURGE_SESSION ((DWORD)0x40080031L)

//
// MessageId: AFRES_AFBKDL_FAIL_CREATE_DISK
//
// MessageText:
//
// Failed to create virtual disk %1!u!. System error=[%2!s!].%0
//
#define AFRES_AFBKDL_FAIL_CREATE_DISK    ((DWORD)0xC0080032L)

//
// MessageId: AFRES_AFBKDL_FAIL_WRITE_DISK_INTERNAL
//
// MessageText:
//
// Failed to write to virtual disk. Internal error=[%1!s!].%0
//
#define AFRES_AFBKDL_FAIL_WRITE_DISK_INTERNAL ((DWORD)0xC0080033L)

//
// MessageId: AFRES_AFBKDL_FAIL_CREATE_DISK_INTERNAL
//
// MessageText:
//
// Failed to create virtual disk %1!u!. Internal error=[%2!s!].%0
//
#define AFRES_AFBKDL_FAIL_CREATE_DISK_INTERNAL ((DWORD)0xC0080034L)

//
// MessageId: AFRES_AFBKDL_BASE_LICENSE_UNAVAILABLE
//
// MessageText:
//
// License failure. Please contact your account representative to obtain a new license.%0
//
#define AFRES_AFBKDL_BASE_LICENSE_UNAVAILABLE ((DWORD)0x80080035L)

//
// MessageId: AFRES_AFBKDL_FAIL_TO_MERGE_SESSIONS
//
// MessageText:
//
// Merge Sessions Failed. Retention count setting=%1!d!, System error=[%2!s!].%0
//
#define AFRES_AFBKDL_FAIL_TO_MERGE_SESSIONS ((DWORD)0xC0080036L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_PRECMD
//
// MessageText:
//
// Executing the pre backup command. (CMD=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_PRECMD   ((DWORD)0x40080037L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_POSTSNAPSHOTCMD
//
// MessageText:
//
// Executing the post snapshot command. (CMD=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_POSTSNAPSHOTCMD ((DWORD)0x40080038L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_POSTCMD
//
// MessageText:
//
// Executing the post backup command. (CMD=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_POSTCMD  ((DWORD)0x40080039L)

//
// MessageId: AFRES_AFBKDL_REMOVEVABLE_VOLUME_SKIPPED
//
// MessageText:
//
// Removable volume %1!s! is skipped.%0
//
#define AFRES_AFBKDL_REMOVEVABLE_VOLUME_SKIPPED ((DWORD)0x8008003AL)

//
// MessageId: AFRES_AFBKDL_FAIL_LOCATE_DEST_NULL_USER
//
// MessageText:
//
// Failed to locate backup destination. (Path=[%1!s!], EC=[%2!d!: %3!s!])%0
//
#define AFRES_AFBKDL_FAIL_LOCATE_DEST_NULL_USER ((DWORD)0xC008003BL)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_DRIVER
//
// MessageText:
//
// Converting to a verify backup because unexpected shutdown or error occurred during last backup.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_DRIVER ((DWORD)0x4008003CL)

//
// MessageId: AFRES_AFBKDL_THRESHOLD_HIT
//
// MessageText:
//
// User configured value for the destination threshold has been reached.%0
//
#define AFRES_AFBKDL_THRESHOLD_HIT       ((DWORD)0x8008003DL)

//
// MessageId: AFRES_AFBKDL_UNRECOGNIZED_FS
//
// MessageText:
//
// unrecognized%0
//
#define AFRES_AFBKDL_UNRECOGNIZED_FS     ((DWORD)0x4008003EL)

//
// MessageId: AFRES_AFBKDL_VOLUME_LOCKED
//
// MessageText:
//
// The %1!s! is encrypted by BitLocker Drive Encryption, please unlock it before submitting backup job.%0
//
#define AFRES_AFBKDL_VOLUME_LOCKED       ((DWORD)0xC008003FL)

//
// MessageId: AFRES_AFBKDL_COMPRESSRATIO
//
// MessageText:
//
// Compression reduced the size by %1!d!%2!c!%3!02d!%% to %4!s!.%0
//
#define AFRES_AFBKDL_COMPRESSRATIO       ((DWORD)0x40080040L)

//
// MessageId: AFRES_AFBKDL_NOBMRLIC
//
// MessageText:
//
// There is no Bare Metal Recovery to Original license in current machine.%0
//
#define AFRES_AFBKDL_NOBMRLIC            ((DWORD)0x80080041L)

//
// MessageId: AFRES_AFBKDL_NOBMRAHWLIC
//
// MessageText:
//
// There is no Bare Metal Recovery to Alternate Hardware license in current machine.%0
//
#define AFRES_AFBKDL_NOBMRAHWLIC         ((DWORD)0x80080042L)

//
// MessageId: AFRES_AFBKDL_FAIL_TO_MERGE_SESSIONS_INTERNAL
//
// MessageText:
//
// Merge Sessions Failed. Retention count setting=%1!d!, Internal error=[%2!s!].%0
//
#define AFRES_AFBKDL_FAIL_TO_MERGE_SESSIONS_INTERNAL ((DWORD)0xC0080043L)

//
// MessageId: AFRES_AFBKDL_NO_BLI_LIC_INCR
//
// MessageText:
//
// Your current licensing only supports block-level incremental (BLI) backups at an minimum interval of one per hour. To perform more frequent BLI backups (as often as every 15 minutes), please upgrade your licensing.%0
//
#define AFRES_AFBKDL_NO_BLI_LIC_INCR     ((DWORD)0xC0080044L)

//
// MessageId: AFRES_AFBKDL_NO_BLI_LIC_INCR_TO_FULL
//
// MessageText:
//
// The current licensing will cause ^AU_ProductName_AGENT_SHORT^ to automatically convert your scheduled incremental backup to a full backup if there has not been a full backup performed during the last 7 days. To avoid forced full backups, please upgrade your licensing.%0
//
#define AFRES_AFBKDL_NO_BLI_LIC_INCR_TO_FULL ((DWORD)0x80080045L)

//
// MessageId: AFRES_AFBKDL_NO_BLI_LIC_RESYNC_TO_FULL
//
// MessageText:
//
// The current licensing will cause ^AU_ProductName_AGENT_SHORT^ to automatically convert your scheduled verify backup to a full backup as there has not been a full backup performed during the last 7 days. To avoid forced full backups, please upgrade your licensing.%0
//
#define AFRES_AFBKDL_NO_BLI_LIC_RESYNC_TO_FULL ((DWORD)0x80080046L)

//
// MessageId: AFRES_AFBKDL_DEST_FREE_SPACE_TOO_LESS
//
// MessageText:
//
// Free space on %1!s! is too low and the backup may fail. Free up some space or change the backup destination to another volume.%0
//
#define AFRES_AFBKDL_DEST_FREE_SPACE_TOO_LESS ((DWORD)0x80080047L)

//
// MessageId: AFRES_AFBKDL_VOLUME_FOR_VERIFY_BACKUP
//
// MessageText:
//
// Will perform a Verify Backup of volume %1!s! because the last system shutdown was unexpected or the removable device is reconnected.%0
//
#define AFRES_AFBKDL_VOLUME_FOR_VERIFY_BACKUP ((DWORD)0x80080048L)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_DRIVER_VERIFY
//
// MessageText:
//
// Convert Incremental Backup to a Verify Backup because the last system shutdown was unexpected or some removable devices are reconnected.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_DRIVER_VERIFY ((DWORD)0x80080049L)

//
// MessageId: AFRES_AFBKDL_SOURCE_VOLUME_NA
//
// MessageText:
//
// Source volume %1!s! is unavailable.%0
//
#define AFRES_AFBKDL_SOURCE_VOLUME_NA    ((DWORD)0x8008004AL)

//
// MessageId: AFRES_AFBKDL_NO_SOURCE_VOLUMES_SELECTED
//
// MessageText:
//
// No source volumes are available for backup.%0
//
#define AFRES_AFBKDL_NO_SOURCE_VOLUMES_SELECTED ((DWORD)0xC008004BL)

//
// MessageId: AFRES_AFBKDL_MERGE_SESSIONS
//
// MessageText:
//
// Session %1!d! is purged because the retention count %2!d! is reached.%0
//
#define AFRES_AFBKDL_MERGE_SESSIONS      ((DWORD)0x4008004CL)

//
// MessageId: AFRES_AFBKDL_FAIL_READ_SHADOW_COPY
//
// MessageText:
//
// Failed to read shadow copy of volume %1!s!, the system error is [%2!s!].%0
//
#define AFRES_AFBKDL_FAIL_READ_SHADOW_COPY ((DWORD)0xC008004DL)

//
// MessageId: AFRES_AFBKDL_CANCEL_MERGE_SESSIONS
//
// MessageText:
//
// The merging sessions process has been canceled, the current session count is %1!d! and the retention count setting is %2!d!.%0
//
#define AFRES_AFBKDL_CANCEL_MERGE_SESSIONS ((DWORD)0x8008004EL)

//
// MessageId: AFRES_AFBKDL_VOLUME_DISK2T_SKIPPED
//
// MessageText:
//
// Source volume %1!s! is skipped because it resides on a disk larger than 2TB. Please use a compressed virtual disk to support a disk larger than 2TB.%0
//
#define AFRES_AFBKDL_VOLUME_DISK2T_SKIPPED ((DWORD)0x8008004FL)

//
// MessageId: AFRES_AFBKDL_DEST_TOTAL_SIZE
//
// MessageText:
//
// Total backup data size is %1!s!.%0
//
#define AFRES_AFBKDL_DEST_TOTAL_SIZE     ((DWORD)0x40080050L)

//
// MessageId: AFRES_AFBKDL_FAIL_WRITE_DISK_INVALID_CMD
//
// MessageText:
//
// Failed to write to virtual disk. Internal error=[%1!s!]. Please check disk space of the backup destination volume, or the network connection to the remote backup destination folder.%0
//
#define AFRES_AFBKDL_FAIL_WRITE_DISK_INVALID_CMD ((DWORD)0xC0080051L)

//
// MessageId: AFRES_AFBKDL_ENCINFO_NONE
//
// MessageText:
//
// Encryption is not enabled.%0
//
#define AFRES_AFBKDL_ENCINFO_NONE        ((DWORD)0x40080052L)

//
// MessageId: AFRES_AFBKDL_ENCINFO_AES128
//
// MessageText:
//
// Encryption type is AES-128.%0
//
#define AFRES_AFBKDL_ENCINFO_AES128      ((DWORD)0x40080053L)

//
// MessageId: AFRES_AFBKDL_ENCINFO_AES192
//
// MessageText:
//
// Encryption type is AES-192.%0
//
#define AFRES_AFBKDL_ENCINFO_AES192      ((DWORD)0x40080054L)

//
// MessageId: AFRES_AFBKDL_ENCINFO_AES256
//
// MessageText:
//
// Encryption type is AES-256.%0
//
#define AFRES_AFBKDL_ENCINFO_AES256      ((DWORD)0x40080055L)

//
// MessageId: AFRES_AFBKDL_ENCINFO_PWD_CHANGE
//
// MessageText:
//
// Convert to full backup because encryption password changed.%0
//
#define AFRES_AFBKDL_ENCINFO_PWD_CHANGE  ((DWORD)0x40080056L)

//
// MessageId: AFRES_AFBKDL_ENCINFO_ALGO_CHANGE
//
// MessageText:
//
// Convert to full backup because encryption type changed.%0
//
#define AFRES_AFBKDL_ENCINFO_ALGO_CHANGE ((DWORD)0x40080057L)

//
// MessageId: AFRES_AFBKDL_PERF_DATA_INIT_FAIL
//
// MessageText:
//
// Failed to initialize performance counter module. Throughput data may not be available.%0
//
#define AFRES_AFBKDL_PERF_DATA_INIT_FAIL ((DWORD)0xC0080058L)

//
// MessageId: AFRES_AFBKDL_INVALID_SESSION_TO_PURGE
//
// MessageText:
//
// Found invalid session. (Session Path=[%1!s!])%0
//
#define AFRES_AFBKDL_INVALID_SESSION_TO_PURGE ((DWORD)0xC0080059L)

//
// MessageId: AFRES_AFBKDL_PURGE_INVALID_SESSION_FAILED
//
// MessageText:
//
// Failed to purge invalid session. (Session Path=[%1!s!])%0
//
#define AFRES_AFBKDL_PURGE_INVALID_SESSION_FAILED ((DWORD)0xC008005AL)

//
// MessageId: AFRES_AFBKDL_PURGE_INVALID_SESSION_SUCCEED
//
// MessageText:
//
// Purge invalid session successfully. (Session Path=[%1!s!])%0
//
#define AFRES_AFBKDL_PURGE_INVALID_SESSION_SUCCEED ((DWORD)0x4008005BL)

//
// MessageId: AFRES_AFBKDL_CLEAR_FORCE_FULL_BACKUP_FLAG
//
// MessageText:
//
// Failed to clear force full backup flag from Registry, please manually remove registry item HKLM\%1!s!\%2!s!.%0
//
#define AFRES_AFBKDL_CLEAR_FORCE_FULL_BACKUP_FLAG ((DWORD)0x4008005CL)

//
// MessageId: AFRES_AFBKDL_FORCE_FULL_BACKUP
//
// MessageText:
//
// Convert to full backup according to force full backup flag. For example, update Virtual Conversion setting will set this flag.%0
//
#define AFRES_AFBKDL_FORCE_FULL_BACKUP   ((DWORD)0x4008005DL)

//
// MessageId: AFRES_AFBKDL_NO_VDDK
//
// MessageText:
//
// VMware Virtual Disk Development Kit not found! Please check if it is installed properly.%0
//
#define AFRES_AFBKDL_NO_VDDK             ((DWORD)0xC008005EL)

//
// MessageId: AFRES_AFBKDL_NO_64BIT_VDDK
//
// MessageText:
//
// The 64 bit binaries are not found in VMware Virtual Disk Development Kit! Please check if it is installed properly.%0
//
#define AFRES_AFBKDL_NO_64BIT_VDDK       ((DWORD)0xC008005FL)

//
// MessageId: AFRES_AFBKDL_VDDK_OLDVERSION
//
// MessageText:
//
// Only VMware Virtual Disk Development Kit 1.2 or later version is supported!%0
//
#define AFRES_AFBKDL_VDDK_OLDVERSION     ((DWORD)0xC0080060L)

//
// MessageId: AFRES_AFBKDL_NO_VIX
//
// MessageText:
//
// VMware VIX not found! Please check if it is installed properly.%0
//
#define AFRES_AFBKDL_NO_VIX              ((DWORD)0xC0080061L)

//
// MessageId: AFRES_AFBKDL_VIX_OLDVERSION
//
// MessageText:
//
// VMware VIX version is lower than 1.1.0. The application cannot perform application log truncation and Pre/Post commands.%0
//
#define AFRES_AFBKDL_VIX_OLDVERSION      ((DWORD)0x80080062L)

//
// MessageId: AFRES_AFBKDL_ENABLE_EXCHGRTCAT
//
// MessageText:
//
// Generate Exchange Granular Restore catalog automatically after each backup: Enabled.%0
//
#define AFRES_AFBKDL_ENABLE_EXCHGRTCAT   ((DWORD)0x40080063L)

//
// MessageId: AFRES_AFBKDL_DISABLE_EXCHGRTCAT
//
// MessageText:
//
// Generate Exchange Granular Restore catalog automatically after each backup: Disabled.%0
//
#define AFRES_AFBKDL_DISABLE_EXCHGRTCAT  ((DWORD)0x40080064L)

//
// MessageId: AFRES_AFBKDL_INCRE_CHANGEDTOFULL
//
// MessageText:
//
// Converting to full backup because disk has been changed since last backup.%0
//
#define AFRES_AFBKDL_INCRE_CHANGEDTOFULL ((DWORD)0x80080065L)

//
// MessageId: AFRES_AFBKDL_VM_GET_VOL_INFO
//
// MessageText:
//
// Get volume information from virtual machine Guest OS.%0
//
#define AFRES_AFBKDL_VM_GET_VOL_INFO     ((DWORD)0x40080066L)

//
// MessageId: AFRES_AFBKDL_VM_GET_VOL_INFO_ERROR
//
// MessageText:
//
// Could not get volume information from virtual machine Guest OS.%0
//
#define AFRES_AFBKDL_VM_GET_VOL_INFO_ERROR ((DWORD)0xC0080067L)

//
// MessageId: AFRES_AFBKDL_VM_CREATE_SNAPSHOT_FAIL
//
// MessageText:
//
// Failed to create virtual machine snapshot.%0
//
#define AFRES_AFBKDL_VM_CREATE_SNAPSHOT_FAIL ((DWORD)0xC0080068L)

//
// MessageId: AFRES_AFBKDL_VM_GET_APP_INFO
//
// MessageText:
//
// Get application information from virtual machine snapshot.%0
//
#define AFRES_AFBKDL_VM_GET_APP_INFO     ((DWORD)0x40080069L)

//
// MessageId: AFRES_AFBKDL_VM_GET_APP_INFO_ERROR
//
// MessageText:
//
// Could not get application-related information from the virtual machine snapshot. Your backup may not contain application-specific sessions, such as Microsoft SQL Server or Exchange Server. Check the status of the Microsoft Volume Shadow Copy service in the Guest Operating System.%0
//
#define AFRES_AFBKDL_VM_GET_APP_INFO_ERROR ((DWORD)0xC008006AL)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_VDISK
//
// MessageText:
//
// Backing up virtual disk %1!s!.%0
//
#define AFRES_AFBKDL_VM_BACKUP_VDISK     ((DWORD)0x4008006BL)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_VDISK_HW_SNAPSHOT
//
// MessageText:
//
// Backing up virtual disk %1!s! using Hardware snapshot.%0
//
#define AFRES_AFBKDL_VM_BACKUP_VDISK_HW_SNAPSHOT ((DWORD)0x4008006CL)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_VDISK_FINSIH
//
// MessageText:
//
// Finished backing up virtual disk %1!s!.%0
//
#define AFRES_AFBKDL_VM_BACKUP_VDISK_FINSIH ((DWORD)0x4008006DL)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_VDISK_FAIL
//
// MessageText:
//
// Failed to backup virtual disk %1!s!.%0
//
#define AFRES_AFBKDL_VM_BACKUP_VDISK_FAIL ((DWORD)0xC008006EL)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_VDISK_HW_SNAPSHOT_FAIL
//
// MessageText:
//
// Failed to backup virtual disk %1!s! using Hardware snapshot.%0
//
#define AFRES_AFBKDL_VM_BACKUP_VDISK_HW_SNAPSHOT_FAIL ((DWORD)0xC008006FL)

//
// MessageId: AFRES_AFBKDL_VM_SOURCE
//
// MessageText:
//
// Backup source virtual machine %1!s! at %2!s!.%0
//
#define AFRES_AFBKDL_VM_SOURCE           ((DWORD)0x40080070L)

//
// MessageId: AFRES_AFBKDL_VM_PURGE_SQL_LOG
//
// MessageText:
//
// Purge Microsoft SQL Server log from virtual machine.%0
//
#define AFRES_AFBKDL_VM_PURGE_SQL_LOG    ((DWORD)0x40080071L)

//
// MessageId: AFRES_AFBKDL_VM_PURGE_SQL_LOG_ERROR
//
// MessageText:
//
// Could not purge Microsoft SQL Server log from virtual machine.%0
//
#define AFRES_AFBKDL_VM_PURGE_SQL_LOG_ERROR ((DWORD)0xC0080072L)

//
// MessageId: AFRES_AFBKDL_VM_PURGE_EXCH_LOG
//
// MessageText:
//
// Purge Microsoft Exchange Server log from virtual machine.%0
//
#define AFRES_AFBKDL_VM_PURGE_EXCH_LOG   ((DWORD)0x40080073L)

//
// MessageId: AFRES_AFBKDL_VM_PURGE_EXCH_LOG_ERROR
//
// MessageText:
//
// Could not purge Microsoft Exchange Server log from virtual machine.%0
//
#define AFRES_AFBKDL_VM_PURGE_EXCH_LOG_ERROR ((DWORD)0xC0080074L)

//
// MessageId: AFRES_AFBKDL_VM_TOOL_NOT_INSTALLED
//
// MessageText:
//
// VMware Tools is not installed.%0
//
#define AFRES_AFBKDL_VM_TOOL_NOT_INSTALLED ((DWORD)0xC0080075L)

//
// MessageId: AFRES_AFBKDL_VM_TOOL_OUT_OF_DATE
//
// MessageText:
//
// The version of VMware Tools is out-dated and should be upgraded.%0
//
#define AFRES_AFBKDL_VM_TOOL_OUT_OF_DATE ((DWORD)0xC0080076L)

//
// MessageId: AFRES_AFBKDL_VM_TOOL_UP_TO_DATE
//
// MessageText:
//
// VMware Tools is up to date.%0
//
#define AFRES_AFBKDL_VM_TOOL_UP_TO_DATE  ((DWORD)0x40080077L)

//
// MessageId: AFRES_AFBKDL_VM_TOOL_UNEXPECTED_ERROR
//
// MessageText:
//
// Unable to check status of VMware Tools.%0
//
#define AFRES_AFBKDL_VM_TOOL_UNEXPECTED_ERROR ((DWORD)0xC0080078L)

//
// MessageId: AFRES_AFBKDL_VM_POWERED_OFF
//
// MessageText:
//
// Virtual machine is powered off.%0
//
#define AFRES_AFBKDL_VM_POWERED_OFF      ((DWORD)0x40080079L)

//
// MessageId: AFRES_AFBKDL_VM_POWERED_ON
//
// MessageText:
//
// Virtual machine is powered on.%0
//
#define AFRES_AFBKDL_VM_POWERED_ON       ((DWORD)0x4008007AL)

//
// MessageId: AFRES_AFBKDL_VM_SUSPENDED
//
// MessageText:
//
// Virtual machine is suspended.%0
//
#define AFRES_AFBKDL_VM_SUSPENDED        ((DWORD)0x4008007BL)

//
// MessageId: AFRES_AFBKDL_VM_POWER_UNEXPECTED_ERROR
//
// MessageText:
//
// Unable to determine power state of virtual machine.%0
//
#define AFRES_AFBKDL_VM_POWER_UNEXPECTED_ERROR ((DWORD)0xC008007CL)

//
// MessageId: AFRES_AFBKDL_VM_SNAPSHOT_CHANGED
//
// MessageText:
//
// Convert Incremental Backup to a Verify Backup because the virtual machine snapshots either changed from the last backup job or needs consolidation.%0
//
#define AFRES_AFBKDL_VM_SNAPSHOT_CHANGED ((DWORD)0x4008007DL)

//
// MessageId: AFRES_AFBKDL_VM_USER_EVENT_BEGIN_BACKUP
//
// MessageText:
//
// Begin to backup virtual machine %1!s!.%0
//
#define AFRES_AFBKDL_VM_USER_EVENT_BEGIN_BACKUP ((DWORD)0x4008007EL)

//
// MessageId: AFRES_AFBKDL_VM_USER_EVENT_FINISHED_BACKUP
//
// MessageText:
//
// Finished backing up virtual machine %1!s!.%0
//
#define AFRES_AFBKDL_VM_USER_EVENT_FINISHED_BACKUP ((DWORD)0x4008007FL)

//
// MessageId: AFRES_AFBKDL_VM_USER_EVENT_FAILED_BACKUP
//
// MessageText:
//
// Failed to backup virtual machine %1!s!.%0
//
#define AFRES_AFBKDL_VM_USER_EVENT_FAILED_BACKUP ((DWORD)0xC0080080L)

//
// MessageId: AFRES_AFBKDL_VM_SKIP_VOLUME
//
// MessageText:
//
// Skipped volume %1!s!.%0
//
#define AFRES_AFBKDL_VM_SKIP_VOLUME      ((DWORD)0x80080081L)

//
// MessageId: AFRES_AFBKDL_VM_CTKENABLE_FAILED
//
// MessageText:
//
// The application cannot enable changed block tracking on the virtual machine because there are snapshots on the virtual machine. To correct this problem, delete the snapshots and then resubmit the backup.%0
//
#define AFRES_AFBKDL_VM_CTKENABLE_FAILED ((DWORD)0xC0080082L)

//
// MessageId: AFRES_AFBKDL_VM_TAKESNAP_FAILED
//
// MessageText:
//
// Could not take snapshot of the virtual machine. ESX Server/vCenter Server reported the following error: %1!s!.%0
//
#define AFRES_AFBKDL_VM_TAKESNAP_FAILED  ((DWORD)0xC0080083L)

//
// MessageId: AFRES_AFBKDL_SKIP_MERGE_WHEN_NOT_ALLOWED
//
// MessageText:
//
// Merge session is skipped because the session is locked by another operation. Verify if any recovery points are mounted and dismount them.%0
//
#define AFRES_AFBKDL_SKIP_MERGE_WHEN_NOT_ALLOWED ((DWORD)0x80080084L)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_NO_SESS_GUID
//
// MessageText:
//
// Converting to verify backup because last successful backup is missing.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_NO_SESS_GUID ((DWORD)0x80080085L)

//
// MessageId: AFRES_AFBKDL_ENCRYPT_WARNING
//
// MessageText:
//
// This is an upgrade from the previous release. Use the Create Boot Kit utility to create a bootable BMR ISO image that is used to perform a Bare Metal Recovery (BMR) for any recovery points that were created from the current release.%0
//
#define AFRES_AFBKDL_ENCRYPT_WARNING     ((DWORD)0x80080086L)

//
// MessageId: AFRES_AFBKDL_LOCK_SESS_FAILED_WHEN_SESS_MERGED
//
// MessageText:
//
// Unable to lock session %1!u!. The session may have been merged or removed.%0
//
#define AFRES_AFBKDL_LOCK_SESS_FAILED_WHEN_SESS_MERGED ((DWORD)0xC0080087L)

//
// MessageId: AFRES_AFBKDL_LOCK_SESS_FAILED
//
// MessageText:
//
// Unable to lock session %1!u!.%0
//
#define AFRES_AFBKDL_LOCK_SESS_FAILED    ((DWORD)0xC0080088L)

//
// MessageId: AFRES_AFBKDL_NOT_READY_WHEN_FAILED_MERGE_SESSION
//
// MessageText:
//
// It is not ready for backup because previous merge operation is not complete.%0
//
#define AFRES_AFBKDL_NOT_READY_WHEN_FAILED_MERGE_SESSION ((DWORD)0xC0080089L)

//
// MessageId: AFRES_AFBKDL_REPAIR_FAILED_MERGE_FAILED_WHEN_LOCKED
//
// MessageText:
//
// Backup is skipped because session %1!u! is being merged. A makeup job will be scheduled for this skipped backup.%0
//
#define AFRES_AFBKDL_REPAIR_FAILED_MERGE_FAILED_WHEN_LOCKED ((DWORD)0x8008008AL)

//
// MessageId: AFRES_AFBKDL_FAILED_TO_READ_VHD_SYSTEM_ERR
//
// MessageText:
//
// Failed to read data from virtual disk. System error=[%1!s!].%0
//
#define AFRES_AFBKDL_FAILED_TO_READ_VHD_SYSTEM_ERR ((DWORD)0xC008008BL)

//
// MessageId: AFRES_AFBKDL_FAILED_TO_READ_VHD_INTERNAL_ERR
//
// MessageText:
//
// Failed to read data from virtual disk. Internal error=[%1!s!].%0
//
#define AFRES_AFBKDL_FAILED_TO_READ_VHD_INTERNAL_ERR ((DWORD)0xC008008CL)

//
// MessageId: AFRES_AFBKDL_FAILED_TO_READ_VHD_INTERNAL_ERR_EX
//
// MessageText:
//
// Failed to read data from virtual disk. Internal error=[%1!s!]. Please check disk space of the backup destination volume, or the network connection to the remote backup destination folder.%0
//
#define AFRES_AFBKDL_FAILED_TO_READ_VHD_INTERNAL_ERR_EX ((DWORD)0xC008008DL)

//
// MessageId: AFRES_AFBKDL_NON_NTFS_ReFS_VOLUME_SKIPPED
//
// MessageText:
//
// Non NTFS / ReFS volume %1!s! is skipped, the file system is %2!s!. If full volume encryption (for example, Window Bitlocker) is enabled on this volume, unlock / decrypt the volume and try to run backup again.%0
//
#define AFRES_AFBKDL_NON_NTFS_ReFS_VOLUME_SKIPPED ((DWORD)0x8008008EL)

//
// MessageId: AFRES_AFBKDL_VOLUME_SIZE_CHANGE
//
// MessageText:
//
// The size of volume %1!s! (%2!s!) was changed from %3!s! to %4!s! while the backup job was running.%0
//
#define AFRES_AFBKDL_VOLUME_SIZE_CHANGE  ((DWORD)0xC008008FL)

//
// MessageId: AFRES_AFBKDL_VOLUME_SNAPSHOT_BITMAP
//
// MessageText:
//
// Failed to get the snapshot bitmap for volume %1!s!(%2!s!). This can be caused by not enough space for the storage area of the snapshot. Check the application/system event log for more information.%0
//
#define AFRES_AFBKDL_VOLUME_SNAPSHOT_BITMAP ((DWORD)0xC0080090L)

//
// MessageId: AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STORE_CHANGED
//
// MessageText:
//
// Convert the current backup to a Full Backup because a new data store has been used for backup.%0
//
#define AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STORE_CHANGED ((DWORD)0x80080091L)

//
// MessageId: AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_LOCAL_TO_DATA_STORE
//
// MessageText:
//
// Convert the current backup to a Full Backup because the backup destination has been changed from local disk or shared folder to a data store.%0
//
#define AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_LOCAL_TO_DATA_STORE ((DWORD)0x80080092L)

//
// MessageId: AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STORE_TO_LOCAL
//
// MessageText:
//
// Convert the current backup to a Full Backup because the backup destination has been changed from a data store to local disk or shared folder.%0
//
#define AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STORE_TO_LOCAL ((DWORD)0x80080093L)

//
// MessageId: AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STOREOR_PROXY_CHANGED
//
// MessageText:
//
// The current backup job is converted to a Full Backup because a new data store has been used for backup or the backup proxy machine has been changed.%0
//
#define AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STOREOR_PROXY_CHANGED ((DWORD)0x80080094L)

//
// MessageId: AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STORE_TO_LOCAL_PROXY_CHANGED
//
// MessageText:
//
// The current backup job is converted to a Full Backup because the backup destination has been changed from a data store to local disk or shared folder or the backup proxy machine has been changed.%0
//
#define AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_DATA_STORE_TO_LOCAL_PROXY_CHANGED ((DWORD)0x80080095L)

//
// MessageId: AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_LOCAL_TO_DATA_STORE_PROXY_CHANGED
//
// MessageText:
//
// The current backup job is converted to a Full Backup because the backup destination has been changed from local disk or shared folder to a data store or the backup proxy machine has been changed.%0
//
#define AFRES_AFBKDL_CONVERT_TO_FULL_BECAUSE_LOCAL_TO_DATA_STORE_PROXY_CHANGED ((DWORD)0x80080096L)

//
// MessageId: AFRES_AFBKDL_VM_LOGIN_VM_FAILED
//
// MessageText:
//
// Unable to log in to the guest operating system on the virtual machine. ESX/vCenter reports error: %1!s!. This could also happen if your VMware Tools is out-of-date.%0
//
#define AFRES_AFBKDL_VM_LOGIN_VM_FAILED  ((DWORD)0xC0080097L)

//
// MessageId: AFRES_AFBKDL_VM_NO_SQL_WM
//
// MessageText:
//
// Metadata for the Microsoft SQL Server writer was not found.%0
//
#define AFRES_AFBKDL_VM_NO_SQL_WM        ((DWORD)0x40080098L)

//
// MessageId: AFRES_AFBKDL_VM_NO_EXCHG_WM
//
// MessageText:
//
// Metadata for the Microsoft Exchange Server writer was not found.%0
//
#define AFRES_AFBKDL_VM_NO_EXCHG_WM      ((DWORD)0x40080099L)

//
// MessageId: AFRES_AFBKDL_VM_CLNT_WIN_APP
//
// MessageText:
//
// Warning: This backup session cannot be used for application level restore. For virtual machines running client versions of Microsoft Windows operating systems, VMware snapshot technology does not generate application meta-data required to perform application-level restores. If you require application-level restores and are running the client versions of Microsoft Windows operating systems listed, perform a local ^AU_ProductName_AGENT_SHORT^ backup.%0
//
#define AFRES_AFBKDL_VM_CLNT_WIN_APP     ((DWORD)0x8008009AL)

//
// MessageId: AFRES_AFBKDL_NO_VIX_INSTALLED
//
// MessageText:
//
// VMware VIX is not installed. The application cannot perform application log truncation and Pre/Post commands without VMware VIX.%0
//
#define AFRES_AFBKDL_NO_VIX_INSTALLED    ((DWORD)0xC008009BL)

//
// MessageId: AFRES_AFBKDL_PURGE_EXCH_LOG_VM
//
// MessageText:
//
// Microsoft Exchange Server logs will be purged inside virtual machine %1!s! with file pattern %2!s! until log sequence number %3!I64d! is reached.%0
//
#define AFRES_AFBKDL_PURGE_EXCH_LOG_VM   ((DWORD)0x4008009CL)

//
// MessageId: AFRES_AFBKDL_PURGE_SQL_LOG_VM
//
// MessageText:
//
// Microsoft SQL Server logs will be purged inside virtual machine for database %1!s!\%2!s!.%0
//
#define AFRES_AFBKDL_PURGE_SQL_LOG_VM    ((DWORD)0x4008009DL)

//
// MessageId: AFRES_AFBKDL_FAILED_TO_SAVE_CATALOG_SCRIPT
//
// MessageText:
//
// Failed to save the catalog job script. (This failure will not affect backup jobs).%0
//
#define AFRES_AFBKDL_FAILED_TO_SAVE_CATALOG_SCRIPT ((DWORD)0xC008009EL)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT
//
// MessageText:
//
// Unable to identify the blocks that were used or changed on the virtual machine. Please check VMware logs for more details. Performing backup of all blocks.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT        ((DWORD)0xC008009FL)

//
// MessageId: AFRES_AFBKDL_VM_GET_INFO
//
// MessageText:
//
// The application was unable to retrieve information about virtual machine %1!s!.%0
//
#define AFRES_AFBKDL_VM_GET_INFO         ((DWORD)0xC00800A0L)

//
// MessageId: AFRES_AFBKDL_FAIL_CREATE_SESSION_METADATA
//
// MessageText:
//
// Failed to create metadata of backup session.%0
//
#define AFRES_AFBKDL_FAIL_CREATE_SESSION_METADATA ((DWORD)0xC00800A1L)

//
// MessageId: AFRES_AFBKDL_VM_CTKENABLE_FAILED_GENERRAL
//
// MessageText:
//
// The backup failed because changed block tracking cannot be enabled on the specified virtual machine.%0
//
#define AFRES_AFBKDL_VM_CTKENABLE_FAILED_GENERRAL ((DWORD)0xC00800A2L)

//
// MessageId: AFRES_AFBKDL_CHECKVMSRVLICENSE_FAIL
//
// MessageText:
//
// A license check failure occurred on virtual machine ESX server %1!s!.%0
//
#define AFRES_AFBKDL_CHECKVMSRVLICENSE_FAIL ((DWORD)0xC00800A3L)

//
// MessageId: AFRES_AFBKDL_VMSERVER_NOLICENSE
//
// MessageText:
//
// Virtual machine ESX server %1!s! is not licensed.%0
//
#define AFRES_AFBKDL_VMSERVER_NOLICENSE  ((DWORD)0xC00800A4L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_SKIP_UNFORMATED
//
// MessageText:
//
// The application did not back up unformatted volume %1!s!.%0
//
#define AFRES_AFBKDL_VM_VOLUME_SKIP_UNFORMATED ((DWORD)0xC00800A5L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_SKIP_FAT
//
// MessageText:
//
// The application did not back up the metadata for FAT volume %1!s!.%0
//
#define AFRES_AFBKDL_VM_VOLUME_SKIP_FAT  ((DWORD)0xC00800A6L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_SKIP_ISCSI
//
// MessageText:
//
// The application did not back up iSCSI disk volume %1!s!.%0
//
#define AFRES_AFBKDL_VM_VOLUME_SKIP_ISCSI ((DWORD)0xC00800A7L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_SKIP_NOVMDK
//
// MessageText:
//
// The application did not back up volume %1!s! because the virtual disk where the volume resides was not backed up.%0
//
#define AFRES_AFBKDL_VM_VOLUME_SKIP_NOVMDK ((DWORD)0xC00800A8L)

//
// MessageId: AFRES_AFBKDL_VM_VMDK_SKIP_INDEPENDENT
//
// MessageText:
//
// Unable to backup virtual disk %1!s! because it is an independent disk. VMware does not support backing up independent disks.%0
//
#define AFRES_AFBKDL_VM_VMDK_SKIP_INDEPENDENT ((DWORD)0xC00800A9L)

//
// MessageId: AFRES_AFBKDL_VM_VMDK_SKIP_PHYSICAL_MODE
//
// MessageText:
//
// Unable to back up virtual disk %1!s!. The virtual disk is configured in physical compatibility mode raw device mapping (RDM). VMware does not support backing up virtual disks that are configured as physical compatibility mode RDM.%0
//
#define AFRES_AFBKDL_VM_VMDK_SKIP_PHYSICAL_MODE ((DWORD)0x400800AAL)

//
// MessageId: AFRES_AFBKDL_VM_FAIL_DISK_BITMAP
//
// MessageText:
//
// Cannot open VMDK file.%0
//
#define AFRES_AFBKDL_VM_FAIL_DISK_BITMAP ((DWORD)0xC00800ABL)

//
// MessageId: AFRES_AFBKDL_COMPONENT_LICENSE_INVALID
//
// MessageText:
//
// License Check Failure. The %1!s! license is invalid in this machine.%0
//
#define AFRES_AFBKDL_COMPONENT_LICENSE_INVALID ((DWORD)0xC00800ACL)

//
// MessageId: AFRES_AFBKDL_VM_TRANSPORT_MODE
//
// MessageText:
//
// Transport mode is %1!s!.%0
//
#define AFRES_AFBKDL_VM_TRANSPORT_MODE   ((DWORD)0x400800ADL)

//
// MessageId: AFRES_AFBKDL_INSUFFICIENT_FREESPACE_4_MERGE
//
// MessageText:
//
// No sufficient free space on the destination %1!s! to merge sessions %2!u! and %3!u!. Merge will be skipped till enough space is freed for the destination.%0
//
#define AFRES_AFBKDL_INSUFFICIENT_FREESPACE_4_MERGE ((DWORD)0x800800AEL)

//
// MessageId: AFRES_AFBKDL_UNABLE_CHECK_FREESPACE_4_MERGE
//
// MessageText:
//
// Unable to check free space for the destination %1!s!. Will skip to merge sessions %2!u! and %3!u!.%0
//
#define AFRES_AFBKDL_UNABLE_CHECK_FREESPACE_4_MERGE ((DWORD)0x800800AFL)

//
// MessageId: AFRES_AFBKDL_VM_CONNECT_OK
//
// MessageText:
//
// Connected to ESX or vCenter Server successfully.%0
//
#define AFRES_AFBKDL_VM_CONNECT_OK       ((DWORD)0x400800B0L)

//
// MessageId: AFRES_AFBKDL_VM_CONNECT_FAIL
//
// MessageText:
//
// Cannot connect to ESX or vCenter Server. If you are connecting to an ESX server, verify that the VMware Management Service is running on that server. If you are connecting to a vCenter Server, verify that the vCenter Service is running on that server.%0
//
#define AFRES_AFBKDL_VM_CONNECT_FAIL     ((DWORD)0xC00800B1L)

//
// MessageId: AFRES_AFBKDL_VM_CONNECT_UNREACHABLE
//
// MessageText:
//
// Cannot connect to ESX or vCenter Server. The server is unreachable. Verify that the VMware Management Service or vCenter Service is running, the server is connected to the network and the credentials are valid.%0
//
#define AFRES_AFBKDL_VM_CONNECT_UNREACHABLE ((DWORD)0xC00800B2L)

//
// MessageId: AFRES_AFBKDL_VM_CONNECT_INVALID_CRED
//
// MessageText:
//
// Cannot connect to ESX or vCenter Server. Credentials are invalid or incorrect.%0
//
#define AFRES_AFBKDL_VM_CONNECT_INVALID_CRED ((DWORD)0xC00800B3L)

//
// MessageId: AFRES_AFBKDL_VM_VMDKIO_ERROR
//
// MessageText:
//
// Failed to read VMDK. Check network connection between the Proxy and ESX or vCenter Server. If you are using a SAN, check network connection between the Proxy and SAN. Resubmit the backup job.%0
//
#define AFRES_AFBKDL_VM_VMDKIO_ERROR     ((DWORD)0xC00800B4L)

//
// MessageId: AFRES_AFBKDL_JOB_SKIPPED
//
// MessageText:
//
// Backup job is skipped.%0
//
#define AFRES_AFBKDL_JOB_SKIPPED         ((DWORD)0x800800B5L)

//
// MessageId: AFRES_AFBKDL_VM_VRDM_DISK
//
// MessageText:
//
// Virtual disk %1!s! is configured as a virtual compatibility raw device mapping (vRDM) disk and will be backed up as full disk. VMware cannot retrieve the used blocks of disks that are configured as vRDM. If the job is a full backup, the entire disk is included in the backup session. If the job is an incremental backup, only the changed blocks of data are included in the backup session.%0
//
#define AFRES_AFBKDL_VM_VRDM_DISK        ((DWORD)0x800800B6L)

//
// MessageId: AFRES_AFBKDL_VM_DEL_SESSION
//
// MessageText:
//
// The backup cannot continue at this time. The application cannot delete the data related to previous failed backup session because the backup destination is unavailable. The system error is %1!s!. The application will rectify the problem when a backup job runs later.%0?
//
#define AFRES_AFBKDL_VM_DEL_SESSION      ((DWORD)0xC00800B7L)

//
// MessageId: AFRES_AFBKDL_VMSERVER_VERSION_ERROR
//
// MessageText:
//
// The ESX Server version is %1!s!. The application requires ESX Server version 4.0 or later.%0
//
#define AFRES_AFBKDL_VMSERVER_VERSION_ERROR ((DWORD)0xC00800B8L)

//
// MessageId: AFRES_AFBKDL_VM_VERSION_ERROR
//
// MessageText:
//
// The virtual machine version is %1!s!. The application requires virtual machine with version 7 or later to back up the virtual machines.%0
//
#define AFRES_AFBKDL_VM_VERSION_ERROR    ((DWORD)0xC00800B9L)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_VERIFY
//
// MessageText:
//
// The application must reset changed block tracking. The application will perform a verify backup operation instead of an incremental backup operation.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_VERIFY ((DWORD)0x400800BAL)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_NOW
//
// MessageText:
//
// Resetting changed block tracking. Performing backup of all used blocks.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_NOW    ((DWORD)0xC00800BBL)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_SNAPSHOTS
//
// MessageText:
//
// The application cannot reset changed block tracking because there are snapshots present. To reset changed block tracking, delete the snapshots and then resubmit the backup job.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_SNAPSHOTS ((DWORD)0xC00800BCL)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_CREATE_SNAPSHOTS
//
// MessageText:
//
// The application cannot reset changed block tracking because it cannot create a snapshot of the virtual machine.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_CREATE_SNAPSHOTS ((DWORD)0xC00800BDL)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_DEL_SNAPSHOTS
//
// MessageText:
//
// The application cannot reset changed block tracking because it cannot delete the snapshot of the virtual machine.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_DEL_SNAPSHOTS ((DWORD)0xC00800BEL)

//
// MessageId: AFRES_AFBKDL_VM_FAIL_TO_DEL_SNAPSHOT
//
// MessageText:
//
// The application cannot delete the snapshot of the virtual machine. ESX Server/vCenter Server reports the following error: %1!s!.%0
//
#define AFRES_AFBKDL_VM_FAIL_TO_DEL_SNAPSHOT ((DWORD)0xC00800BFL)

//
// MessageId: AFRES_AFBKDL_VM_NFS_DISK
//
// MessageText:
//
// Virtual disk %1!s! is located in an NFS data store and will be backed up as full disk. VMware cannot retrieve the used blocks of disks that are located in NFS data store. If the job is a full backup, the entire disk is included in the backup session. If the job is an incremental backup, only the changed blocks of data are included in the backup session.%0
//
#define AFRES_AFBKDL_VM_NFS_DISK         ((DWORD)0x800800C0L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_DEST_PATH
//
// MessageText:
//
// Backup destination for %1!s! (VM Name is "%2!s!") is %3!s!.%0
//
#define AFRES_AFBKDL_VM_VOLUME_DEST_PATH ((DWORD)0x400800C1L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_DEST_PATH_NO_HOSTNAME
//
// MessageText:
//
// Backup destination for VM "%1!s!" is %2!s!.%0
//
#define AFRES_AFBKDL_VM_VOLUME_DEST_PATH_NO_HOSTNAME ((DWORD)0x400800C2L)

//
// MessageId: AFRES_AFBKDL_VERIFY_SESSION_CORRUPTED
//
// MessageText:
//
// Converting to a Verify backup because some data blocks can be corrupted at session %1!u!.%0
//
#define AFRES_AFBKDL_VERIFY_SESSION_CORRUPTED ((DWORD)0x800800C3L)

//
// MessageId: AFRES_AFBKDL_FAILED_TO_FIND_SHADOWCOPY
//
// MessageText:
//
// Unable to find snapshot for volume [%1!s!]. Check the Volume Shadow Copy Service (VSS) related Windows event log for more details.%0
//
#define AFRES_AFBKDL_FAILED_TO_FIND_SHADOWCOPY ((DWORD)0xC00800C4L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_FS_REFS
//
// MessageText:
//
// The file system of volume %1!s! is ReFS.%0
//
#define AFRES_AFBKDL_VM_VOLUME_FS_REFS   ((DWORD)0x400800C5L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_FS_DEDUPE_NTFS
//
// MessageText:
//
// The file system of volume %1!s! is Deduplication-Enabled NTFS.%0
//
#define AFRES_AFBKDL_VM_VOLUME_FS_DEDUPE_NTFS ((DWORD)0x400800C6L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_SKIP_FAT32
//
// MessageText:
//
// The application did not back up the metadata for FAT32 volume %1!s!.%0
//
#define AFRES_AFBKDL_VM_VOLUME_SKIP_FAT32 ((DWORD)0xC00800C7L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_GENERATE_BITMAP_OK
//
// MessageText:
//
// The bitmap file for file system %1!s!, volume %2!s! has been successfully generated.%0
//
#define AFRES_AFBKDL_VM_VOLUME_GENERATE_BITMAP_OK ((DWORD)0x400800C8L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_SKIP_RAID5
//
// MessageText:
//
// The application did not back up the metadata for RAID5 volume %1!s!.%0
//
#define AFRES_AFBKDL_VM_VOLUME_SKIP_RAID5 ((DWORD)0xC00800C9L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_SKIP_STORAGE_SPACES
//
// MessageText:
//
// The application did not back up the metadata for volumes that were built on the storage pool. As a result, you cannot perform a file-level restore for these volumes.%0
//
#define AFRES_AFBKDL_VM_VOLUME_SKIP_STORAGE_SPACES ((DWORD)0x800800CAL)

//
// MessageId: AFRES_AFBKDL_VM_JOB_NAME_PROCESS_ID
//
// MessageText:
//
// The job [PID: %1!d!] name is %2!s! and the source virtual machine name is %3!s!.%0
//
#define AFRES_AFBKDL_VM_JOB_NAME_PROCESS_ID ((DWORD)0x400800CBL)

//
// MessageId: AFRES_AFBKDL_OS_UPGRADE_DETECTED
//
// MessageText:
//
// Convert the first job after an operating system upgrade to a Verify backup.%0
//
#define AFRES_AFBKDL_OS_UPGRADE_DETECTED ((DWORD)0x400800CCL)

//
// MessageId: AFRES_AFBKDL_FAIL_TO_BACKUP_METADATA
//
// MessageText:
//
// Unable to back up the sub session metadata for volume %1!s!, System error=[%2!s!].%0
//
#define AFRES_AFBKDL_FAIL_TO_BACKUP_METADATA ((DWORD)0xC00800CDL)

//
// MessageId: AFRES_AFBKDL_VOLUME_SEPCIAL_BITMAP
//
// MessageText:
//
// Failed to get the bitmap for volume %1!s! (%2!s!). Check the application/system event log for more information.%0
//
#define AFRES_AFBKDL_VOLUME_SEPCIAL_BITMAP ((DWORD)0xC00800CEL)

//
// MessageId: AFRES_AFBKDL_VM_VDDKEnforceNBD
//
// MessageText:
//
// The backup operation is enforced to use NBD transport mode for VDDK IO.%0
//
#define AFRES_AFBKDL_VM_VDDKEnforceNBD   ((DWORD)0x400800CFL)

//
// MessageId: AFRES_AFBKDL_VM_DISK_SIZE_CHANGE
//
// MessageText:
//
// Virtual disk %1!s! was added recently, or, the overall size of the virtual disk changed. Host-Based VM Backup will now perform a full backup of the virtual disk.%0
//
#define AFRES_AFBKDL_VM_DISK_SIZE_CHANGE ((DWORD)0x800800D0L)

//
// MessageId: AFRES_AFBKDL_BMR_FAILURE_SYSTEM_MIRRIOR
//
// MessageText:
//
// The system volume [%1!s!] is configured as a mirrored volume. As a result, the system will fail to boot if you are attempting to restore data using BMR. %0
//
#define AFRES_AFBKDL_BMR_FAILURE_SYSTEM_MIRRIOR ((DWORD)0x800800D1L)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_VMIMAGE_ERROR
//
// MessageText:
//
// Converted to a Verify Backup. Failed to generate the file system catalog for the last session.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_VMIMAGE_ERROR ((DWORD)0x800800D2L)

//
// MessageId: AFRES_AFBKDL_EXCLUDE_VOLUME
//
// MessageText:
//
// Failed to get physical location of volume [%1!s!]. This volume will be excluded from the backup.%0
//
#define AFRES_AFBKDL_EXCLUDE_VOLUME      ((DWORD)0x800800D3L)

//
// MessageId: AFRES_AFBKDL_EXCLUDE_DS_COMMON_PATH
//
// MessageText:
//
// Volume [%1!s!] contains a common path for data store [%2!s!]. As a result, it will be excluded from the backup job.%0
//
#define AFRES_AFBKDL_EXCLUDE_DS_COMMON_PATH ((DWORD)0x800800D4L)

//
// MessageId: AFRES_AFBKDL_EXCLUDE_DS_HASH_PATH
//
// MessageText:
//
// Volume [%1!s!] contains a hash file path for data store [%2!s!]. As a result, it will be excluded from the backup job.%0
//
#define AFRES_AFBKDL_EXCLUDE_DS_HASH_PATH ((DWORD)0x800800D5L)

//
// MessageId: AFRES_AFBKDL_EXCLUDE_DS_DATA_PATH
//
// MessageText:
//
// Volume [%1!s!] contains a data file path for data store [%2!s!]. As a result, it will be excluded from the backup job.%0
//
#define AFRES_AFBKDL_EXCLUDE_DS_DATA_PATH ((DWORD)0x800800D6L)

//
// MessageId: AFRES_AFBKDL_EXCLUDE_DS_INDEX_PATH
//
// MessageText:
//
// Volume [%1!s!] contains an index file path for data store [%2!s!]. As a result, it will be excluded from the backup job.%0
//
#define AFRES_AFBKDL_EXCLUDE_DS_INDEX_PATH ((DWORD)0x800800D7L)

//
// MessageId: AFRES_AFBKDL_NOT_ENOUGH_SPACE_INSTALL_PATH
//
// MessageText:
//
// The space is not enough on the VM backup proxy machine where the agent is installed.%0
//
#define AFRES_AFBKDL_NOT_ENOUGH_SPACE_INSTALL_PATH ((DWORD)0xC00800D8L)

//
// MessageId: AFRES_AFBKDL_DATA_BACKUP_COMPLETED
//
// MessageText:
//
// Data backup successfully completed.%0
//
#define AFRES_AFBKDL_DATA_BACKUP_COMPLETED ((DWORD)0x400800D9L)

//
// MessageId: AFRES_AFBKDL_COLLECT_BMRINFO_STARTED
//
// MessageText:
//
// Start collecting Bare Metal Recovery information.%0
//
#define AFRES_AFBKDL_COLLECT_BMRINFO_STARTED ((DWORD)0x400800DAL)

//
// MessageId: AFRES_AFBKDL_COMPRESS_LEVEL_CHANGE
//
// MessageText:
//
// Converting to a full backup because the compression level has been changed.%0
//
#define AFRES_AFBKDL_COMPRESS_LEVEL_CHANGE ((DWORD)0x800800DBL)

//
// MessageId: AFRES_AFBKDL_RESYNC_CHANGEDTOFULL
//
// MessageText:
//
// Converting the verify backup to a full backup because deduplication is enabled on the data store.%0
//
#define AFRES_AFBKDL_RESYNC_CHANGEDTOFULL ((DWORD)0x800800DCL)

//
// MessageId: AFRES_AFBKDL_BMR_FAILURE_BOOT_MIRRIOR
//
// MessageText:
//
// The boot volume [%1!s!] is configured as a mirrored volume. As a result, the system will fail to boot if you are attempting to restore data using BMR. %0
//
#define AFRES_AFBKDL_BMR_FAILURE_BOOT_MIRRIOR ((DWORD)0x800800DDL)

//
// MessageId: AFRES_AFBKDL_VM_JOB_PROCESS_ID
//
// MessageText:
//
// The job [PID: %1!d!] started and the source virtual machine name is %2!s!.%0
//
#define AFRES_AFBKDL_VM_JOB_PROCESS_ID   ((DWORD)0x400800DEL)

//
// MessageId: AFRES_AFBKDL_SESSION_DATA_FORMAT_STANDARD
//
// MessageText:
//
// The session data format is standard.%0
//
#define AFRES_AFBKDL_SESSION_DATA_FORMAT_STANDARD ((DWORD)0x400800DFL)

//
// MessageId: AFRES_AFBKDL_SESSION_DATA_FORMAT_ADVANCED
//
// MessageText:
//
// The session data format is advanced.%0
//
#define AFRES_AFBKDL_SESSION_DATA_FORMAT_ADVANCED ((DWORD)0x400800E0L)

//
// MessageId: AFRES_AFBKDL_SESSION_DATA_FORMAT_DEDUPE
//
// MessageText:
//
// The session data format is advanced with deduplication enabled.%0
//
#define AFRES_AFBKDL_SESSION_DATA_FORMAT_DEDUPE ((DWORD)0x400800E1L)

//
// MessageId: AFRES_AFBKDL_DESTINATION_TYPE_DATASTORE
//
// MessageText:
//
// The destination type is a data store.%0
//
#define AFRES_AFBKDL_DESTINATION_TYPE_DATASTORE ((DWORD)0x400800E2L)

//
// MessageId: AFRES_AFBKDL_DESTINATION_TYPE_NONDATASTORE
//
// MessageText:
//
// The destination type is a non-data store.%0
//
#define AFRES_AFBKDL_DESTINATION_TYPE_NONDATASTORE ((DWORD)0x400800E3L)

//
// MessageId: AFRES_AFBKDL_FAILED_TO_SAVE_SESSION_KEY_FILE
//
// MessageText:
//
// Failed to save session key file. (EC=[%1!08x!])%0
//
#define AFRES_AFBKDL_FAILED_TO_SAVE_SESSION_KEY_FILE ((DWORD)0xC00800E4L)

//
// MessageId: AFRES_AFBKDL_INSUFFICIENT_SPACE_ON_D2DHOME
//
// MessageText:
//
// The free space on drive %1!s! is less than %2!d! MB. As a result backup might fail.%0
//
#define AFRES_AFBKDL_INSUFFICIENT_SPACE_ON_D2DHOME ((DWORD)0x800800E5L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_REMOTE
//
// MessageText:
//
// Backup in remote mode on Hyper-V host %1!s!.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_REMOTE ((DWORD)0x400800E6L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CONNECT_FAILED
//
// MessageText:
//
// Failed to connect to Hyper-V host %1!s!.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CONNECT_FAILED ((DWORD)0xC00800E7L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_STUB_CONNECT_FAILED
//
// MessageText:
//
// Failed to connect to the Hyper-V backup utility.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_STUB_CONNECT_FAILED ((DWORD)0xC00800E8L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPARE_BACKUP
//
// MessageText:
//
// Failed to prepare for backup of the virtual machine.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPARE_BACKUP ((DWORD)0xC00800E9L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_STUB_CONNECTED
//
// MessageText:
//
// Successfully connected to the Hyper-V backup utility on host %1!s!.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_STUB_CONNECTED ((DWORD)0x400800EAL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_HEALTHY
//
// MessageText:
//
// The current version of the Change Block Tracking (CBT) feature on Hyper-V host %1!s! is %2!s!.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_HEALTHY ((DWORD)0x400800EBL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_FAILED
//
// MessageText:
//
// The status of the Change Block Tracking (CBT) functionality is Inactive on the Hyper-V host and cannot be reset for the current backup job. As a result, if the next backup job is an Incremental Backup, it will automatically be changed to a Verify Backup.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_FAILED ((DWORD)0x800800ECL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_DISK_FAILED
//
// MessageText:
//
// Failed to perform backup of virtual disk %1!s!. System error=[%2!s!].%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_DISK_FAILED ((DWORD)0xC00800EDL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_DISK_FINISHED
//
// MessageText:
//
// Backup of virtual disk %1!s! successfully completed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_DISK_FINISHED ((DWORD)0x400800EEL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CBT_FAIL_VERIFY
//
// MessageText:
//
// The Change Block Tracking feature failed for the previous backup. As a result the Incremental Backup will be changed to a Verify Backup.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CBT_FAIL_VERIFY ((DWORD)0x800800EFL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_INIT_RESOURCE_FAILED
//
// MessageText:
//
// Failed to initialize the Hyper-V environment.%0
//
#define AFRES_AFBKDL_HYPERV_VM_INIT_RESOURCE_FAILED ((DWORD)0xC00800F0L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_FAILED_INC
//
// MessageText:
//
// The Change Block Tracking feature is Inactive on the Hyper-V host. As a result, redundant data may be backed up and if the next backup job is an Incremental Backup, it will automatically be changed to a Verify Backup.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_FAILED_INC ((DWORD)0x800800F1L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_LOCAL
//
// MessageText:
//
// Backup in local mode on Hyper-V host %1!s!.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_LOCAL  ((DWORD)0x400800F2L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_INIT_RESOURCE
//
// MessageText:
//
// Initializing the Hyper-V environment.%0
//
#define AFRES_AFBKDL_HYPERV_VM_INIT_RESOURCE ((DWORD)0x400800F3L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_STUB_OUT_OF_RESPONSE
//
// MessageText:
//
// The Hyper-V backup utility is not responding.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_STUB_OUT_OF_RESPONSE ((DWORD)0xC00800F4L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_VSS_METHOD_APPLICATION_CONSISTENT
//
// MessageText:
//
// Performing a consistent backup using Hyper-V VSS writer.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_VSS_METHOD_APPLICATION_CONSISTENT ((DWORD)0x400800F5L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_VSS_METHOD_CRASH_CONSISTENT
//
// MessageText:
//
// The Hyper-V VSS writer does not support the taking of data consistency snapshots on this virtual machine. As a result, the backed up data may not be in a consistent state. (For more information about creating application-consistent snapshots, refer to the product documentation).%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_VSS_METHOD_CRASH_CONSISTENT ((DWORD)0x800800F6L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_TAKE_SNAPSHOT
//
// MessageText:
//
// Taking VSS snapshot.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_TAKE_SNAPSHOT ((DWORD)0x400800F7L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_TAKE_SNAPSHOT_FAIL
//
// MessageText:
//
// Failed to take VSS snapshot. System error=[%1!s!].%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_TAKE_SNAPSHOT_FAIL ((DWORD)0xC00800F8L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_OPEN_DISK_FILE_FAIL
//
// MessageText:
//
// Failed to open virtual disk file [%1!s!]. System error=[%2!s!].%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_OPEN_DISK_FILE_FAIL ((DWORD)0xC00800F9L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PHYSICAL_DISK_NOT_BACKUP
//
// MessageText:
//
// Backup of physical hard disks are not supported. ^AU_ProductName_HBVB_SHORT^ will skip any physical hard disks that are attached to this virtual machine.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PHYSICAL_DISK_NOT_BACKUP ((DWORD)0x800800FAL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_DISK_START
//
// MessageText:
//
// Backing up virtual disk %1!s!.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_DISK_START ((DWORD)0x400800FBL)

//
// MessageId: AFRES_AFBKDL_VM_RECONFIG_FAILED
//
// MessageText:
//
// There was an error reported during the reconfiguration of the virtual machine to enable the "disk.EnableUUID" parameter (which is required for an application-consistent backup). This could occur when the virtual environment has recovered from an error (e.g. an unexpected power cycle of the ESX server while the VM was running). This error can be resolved by shutting down the VM and running a new backup job. (To reduce the down time of the VM, the VM can be powered on during or after the "Taking Snapshot" phase of the new backup job).%0
//
#define AFRES_AFBKDL_VM_RECONFIG_FAILED  ((DWORD)0xC00800FCL)

//
// MessageId: AFRES_AFBKDL_DAYLY_BACKUP
//
// MessageText:
//
// This is a daily backup.%0
//
#define AFRES_AFBKDL_DAYLY_BACKUP        ((DWORD)0x400800FDL)

//
// MessageId: AFRES_AFBKDL_WEEKLY_BACKUP
//
// MessageText:
//
// This is a weekly backup.%0
//
#define AFRES_AFBKDL_WEEKLY_BACKUP       ((DWORD)0x400800FEL)

//
// MessageId: AFRES_AFBKDL_MONTHLY_BACKUP
//
// MessageText:
//
// This is a monthly backup.%0
//
#define AFRES_AFBKDL_MONTHLY_BACKUP      ((DWORD)0x400800FFL)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_1ST
//
// MessageText:
//
// The reset of the changed block tracking function has started for the first backup job.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_1ST    ((DWORD)0x40080100L)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_1ST_FAIL
//
// MessageText:
//
// Failed to reset the changed block tracking function for the first backup job.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_1ST_FAIL ((DWORD)0xC0080101L)

//
// MessageId: AFRES_AFBKDL_VM_RESET_CBT_1ST_SUCCEED
//
// MessageText:
//
// The changed block tracking function for the first backup job has been successfully reset.%0
//
#define AFRES_AFBKDL_VM_RESET_CBT_1ST_SUCCEED ((DWORD)0x40080102L)

//
// MessageId: AFRES_AFBKDL_PHASE_TAKING_SNAPSHOT_CBT
//
// MessageText:
//
// Taking snapshot to reset the changed block tracking function ...%0
//
#define AFRES_AFBKDL_PHASE_TAKING_SNAPSHOT_CBT ((DWORD)0x40080103L)

//
// MessageId: AFRES_AFBKDL_PHASE_DELETING_SNAPSHOT_CBT
//
// MessageText:
//
// Deleting snapshot to reset the changed block tracking function ...%0
//
#define AFRES_AFBKDL_PHASE_DELETING_SNAPSHOT_CBT ((DWORD)0x40080104L)

//
// MessageId: AFRES_AFBKDL_CHECK_PERMISSION_FAIL
//
// MessageText:
//
// The user %1!s! may not have sufficient permissions to perform backup. A user with administrative privileges is recommended.%0
//
#define AFRES_AFBKDL_CHECK_PERMISSION_FAIL ((DWORD)0x80080105L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_VSS_REPORT_FAILURE
//
// MessageText:
//
// The Hyper-V VSS writer has encountered an error when processing this virtual machine. (For more information about Hyper-V VSS writer errors, refer to the product documentation).%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_VSS_REPORT_FAILURE ((DWORD)0xC0080106L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_BACKUP_CONFIGURATION_FILE_FAIL
//
// MessageText:
//
// Failed to back up the VM configuration file %1!s!. System error=[%2!s!].%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_BACKUP_CONFIGURATION_FILE_FAIL ((DWORD)0xC0080107L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_001
//
// MessageText:
//
// Failed to connect Hyper-V server %1!s!. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_001 ((DWORD)0x80080108L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_002
//
// MessageText:
//
// Failed to get virtual machine by GUID %1!s! from Hyper-V server %2!s!. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_002 ((DWORD)0x80080109L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_003
//
// MessageText:
//
// Failed to get virtual machine host name. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_003 ((DWORD)0x8008010AL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_004
//
// MessageText:
//
// The virtual machine is not in powered on state. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_004 ((DWORD)0x8008010BL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_005
//
// MessageText:
//
// Failed to connect to the virtual machine using host name %1!s!. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_005 ((DWORD)0x8008010CL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_006
//
// MessageText:
//
// The virtual machine GUID is not expected. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_006 ((DWORD)0x8008010DL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_007
//
// MessageText:
//
// The virtual machine name is not expected. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_007 ((DWORD)0x8008010EL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_008
//
// MessageText:
//
// The guest OS of virtual machine is not a Windows machine. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_008 ((DWORD)0x8008010FL)

//
// MessageId: AFRES_AFBKDL_VM_BK_PREPOST_DISK_ENABLEUUID
//
// MessageText:
//
// The disk.EnableUUID parameter for virtual machine %1!s! was configured for application-level quiescing.%0
//
#define AFRES_AFBKDL_VM_BK_PREPOST_DISK_ENABLEUUID ((DWORD)0x40080110L)

//
// MessageId: AFRES_AFBKDL_VM_BK_DONOT_RECONFIG_DISKUUID
//
// MessageText:
//
// User specified not to reconfigure the disk.EnableUUID parameter for virtual machine %1!s!. As a result, the backed up data may not be in a consistent state if the parameter was not set.%0
//
#define AFRES_AFBKDL_VM_BK_DONOT_RECONFIG_DISKUUID ((DWORD)0x40080111L)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_FS_DEDUPE_NTFS_2012_R2
//
// MessageText:
//
// The file system of volume %1!s! is a deduplication-enabled NTFS. Due to a limitation from Microsoft, the file-level restore in this volume from a non Windows 2012 R2 proxy will fail. (For more information about troubleshooting this problem, refer to the Solutions Guide document).%0
//
#define AFRES_AFBKDL_VM_VOLUME_FS_DEDUPE_NTFS_2012_R2 ((DWORD)0x40080112L)

//
// MessageId: AFRES_AFBKDL_TOTAL_THROUGHPUT_4_VERIFY_BK
//
// MessageText:
//
// Verify backup job scanned a total of %1!s! of source data and processed %2!s! changed data in %3!s!. The verify backup job throughput was %4!s!/min.%0
//
#define AFRES_AFBKDL_TOTAL_THROUGHPUT_4_VERIFY_BK ((DWORD)0x40080113L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_COLLECT_BACKUP_DATA
//
// MessageText:
//
// Collecting backup data.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_COLLECT_BACKUP_DATA ((DWORD)0x40080114L)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_BLIDRIVER_NO_INSTALL
//
// MessageText:
//
// Backup type changed to a Verify Backup. Unable to communicate with the Change Tracking Driver because it was either not installed or was not installed properly. %0
//
#define AFRES_AFBKDL_METHOD_CHANGE_BLIDRIVER_NO_INSTALL ((DWORD)0x80080115L)

//
// MessageId: AFRES_AFBKDL_FREE_EDITON_LISENCE
//
// MessageText:
//
// This is a ‘No-Charge’ Edition of arcserve UDP for workstations, and as a result is running in a limited capability mode. To upgrade to the arcserve UDP Workstation Edition with full functionality, please visit arcserve.com or contact your partner/sales representative.%0
//
#define AFRES_AFBKDL_FREE_EDITON_LISENCE ((DWORD)0x80080116L)

//
// MessageId: AFRES_AFBKDL_FREE_EDITON_NO_SURPPORT_RPS
//
// MessageText:
//
// The ‘No-Charge’ Edition of arcserve UDP for workstation doesn't support RPS destination, please change backup destination to non-RPS destination.%0
//
#define AFRES_AFBKDL_FREE_EDITON_NO_SURPPORT_RPS ((DWORD)0xC0080117L)

//
// MessageId: AFRES_AFBKDL_HANG_DETECTED
//
// MessageText:
//
// The application with process Id %1!d! failed to respond within %2!d! second(s). Please stop the process and retry running the job.%0
//
#define AFRES_AFBKDL_HANG_DETECTED       ((DWORD)0x80080118L)

//
// MessageId: AFRES_AFBKDL_JOB_SUBMITTED_CHILD
//
// MessageText:
//
// Backup job %1!d! for virtual machine %2!s!(%3!s!) has been submitted.%0
//
#define AFRES_AFBKDL_JOB_SUBMITTED_CHILD ((DWORD)0x40080119L)

//
// MessageId: AFRES_AFBKDL_JOB_SUBMITTED_CHILD_FAILED
//
// MessageText:
//
// Failed to submit backup job %1!d! for virtual machine %2!s!(%3!s!).%0
//
#define AFRES_AFBKDL_JOB_SUBMITTED_CHILD_FAILED ((DWORD)0xC008011AL)

//
// MessageId: AFRES_AFBKDL_JOB_INCOMPELETE
//
// MessageText:
//
// Backup job was incompelete.%0
//
#define AFRES_AFBKDL_JOB_INCOMPELETE     ((DWORD)0x8008011BL)

//
// MessageId: AFRES_AFBKDL_JOB_FAILED_CHILD
//
// MessageText:
//
// Backup job %1!d! for virtual machine %2!s!(%3!s!) failed.%0
//
#define AFRES_AFBKDL_JOB_FAILED_CHILD    ((DWORD)0xC008011CL)

//
// MessageId: AFRES_AFBKDL_JOB_FINISHED_CHILD
//
// MessageText:
//
// Backup job %1!d! for virtual machine %2!s!(%3!s!) completed successfully.%0
//
#define AFRES_AFBKDL_JOB_FINISHED_CHILD  ((DWORD)0x4008011DL)

//
// MessageId: AFRES_AFBKDL_JOB_CANCELLED_CHILD
//
// MessageText:
//
// Backup job %1!d! for virtual machine %2!s!(%3!s!) canceled.%0
//
#define AFRES_AFBKDL_JOB_CANCELLED_CHILD ((DWORD)0x8008011EL)

//
// MessageId: AFRES_AFBKND_JOB_CRASHED_CHILD
//
// MessageText:
//
// Job %1!d! for virtual machine %2!s!(%3!s!) crashed.%0
//
#define AFRES_AFBKND_JOB_CRASHED_CHILD   ((DWORD)0xC008011FL)

//
// MessageId: AFRES_AFBKDL_JOB_FAILED_DEST_UNDER_DELETE
//
// MessageText:
//
// Backup job failed because of backup destination is under deletion.%0
//
#define AFRES_AFBKDL_JOB_FAILED_DEST_UNDER_DELETE ((DWORD)0xC0080120L)

//
// MessageId: AFRES_AFBKND_SW_SNAPSHOT_FAILED
//
// MessageText:
//
// Software Snapshot Failed.%0
//
#define AFRES_AFBKND_SW_SNAPSHOT_FAILED  ((DWORD)0xC0080121L)

//
// MessageId: AFRES_AFBKND_HW_SNAPSHOT_FAILED
//
// MessageText:
//
// Hardware Snapshot Failed. Falling back to Software Snapshot.%0
//
#define AFRES_AFBKND_HW_SNAPSHOT_FAILED  ((DWORD)0x80080122L)

//
// MessageId: AFRES_AFBKND_TRANSPORT_SNAPSHOT_FAILED
//
// MessageText:
//
// Tranpsortable Snapshot Failed. Falling back to Hardware Snapshot.%0
//
#define AFRES_AFBKND_TRANSPORT_SNAPSHOT_FAILED ((DWORD)0x80080123L)

//
// MessageId: AFRES_AFBKND_RESET_CBT_VMDK_SIZE_CHANGE
//
// MessageText:
//
// The size of virtual disk %1!s! has changed. The next time a backup job is performed, ^AU_ProductName_HBVB_SHORT^ will reset the Changed Block Tracking (CBT) functionality and automatically perform a Verify Backup.%0
//
#define AFRES_AFBKND_RESET_CBT_VMDK_SIZE_CHANGE ((DWORD)0x80080124L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_BACKUP_TYPE_PRIVATE
//
// MessageText:
//
// The VM will be backed up in the standalone mode.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_BACKUP_TYPE_PRIVATE ((DWORD)0x40080125L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_BACKUP_TYPE_CLUSTER
//
// MessageText:
//
// The VM will be backed up in the cluster mode.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_BACKUP_TYPE_CLUSTER ((DWORD)0x40080126L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_VERSION_FAILED
//
// MessageText:
//
// The current backup job has been denied because of a CBT compatibility problem internal to the Hyper-V host. Please upgrade all of your backup proxies which are used to protect this Hyper-V host to the latest version to resolve this problem and continue your backups.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CBT_MODULE_VERSION_FAILED ((DWORD)0x80080127L)

//
// MessageId: AFRES_AFBKDL_CBT_INACTIVE
//
// MessageText:
//
// Converting the Incremental Backup to a Verify Backup because the Change Block Tracking (CBT) functionality on the Hyper-V host was either Inactive or denied to serve this backup job.%0
//
#define AFRES_AFBKDL_CBT_INACTIVE        ((DWORD)0x80080128L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_UNSUPPORTED_HYPERV_VERSION_2008
//
// MessageText:
//
// ^AU_ProductName_HBVB_SHORT^ does not support the protection of Windows 2008 Hyper-V virtual machines. You need to upgrade your Hyper-V host to a Windows 2008 R2 operating system or later.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_UNSUPPORTED_HYPERV_VERSION_2008 ((DWORD)0xC0080129L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_UNSUPPORTED_HYPERV_VERSION_CSV_2008R2
//
// MessageText:
//
// ^AU_ProductName_HBVB_SHORT^ does not support the protection of Windows 2008 R2 Hyper-V cluster virtual machines. You need to upgrade your Hyper-V host to a Windows 2012 operating system or later.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_UNSUPPORTED_HYPERV_VERSION_CSV_2008R2 ((DWORD)0xC008012AL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CBT_CURABLE_FAIL
//
// MessageText:
//
// The Change Block Tracking function is Inactive and has been reset on the Hyper-V host.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CBT_CURABLE_FAIL ((DWORD)0x8008012BL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_CROSS_STORAGE
//
// MessageText:
//
// The virtual machine has part of its configuration file and virtual disk files in Cluster Shared Volumes (CSV) and the other part in a local disk. This situation is not supported by the Hyper-V VSS Writer.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_CROSS_STORAGE ((DWORD)0xC008012CL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_NODE_UPHEAVAL
//
// MessageText:
//
// The Change Block Tracking (CBT) function failed. One or more clustered nodes were added or removed during the backup job.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_NODE_UPHEAVAL ((DWORD)0x8008012DL)

//
// MessageId: AFRES_AFBKDL_HBBUEXCH_PROXYVER_NOT64
//
// MessageText:
//
// The backup proxy host [%1!s!] is not on a 64 bit platform. As a result, the subsequent Exchange catalog job will fail if the Generate Catalog option is enabled in the plan.%0
//
#define AFRES_AFBKDL_HBBUEXCH_PROXYVER_NOT64 ((DWORD)0x8008012EL)

//
// MessageId: AFRES_AFBKDL_HBBUEXCH_PROXYVER_LOWER_THAN_VM
//
// MessageText:
//
// The Windows version of proxy [%1!s!] is an earlier version than virtual machine [%2!s!]. As a result, the subsequent Exchange catalog job may fail, and you will need to install the related Windows update package to resolve the problem.%0
//
#define AFRES_AFBKDL_HBBUEXCH_PROXYVER_LOWER_THAN_VM ((DWORD)0x8008012FL)

//
// MessageId: AFRES_AFBKDL_HBBUEXCH_VM_NOT_RUNNING
//
// MessageText:
//
// Virtual machine [%1!s!] is not running. As a result, the subsequent Exchange catalog job will not be launched.%0
//
#define AFRES_AFBKDL_HBBUEXCH_VM_NOT_RUNNING ((DWORD)0xC0080130L)

//
// MessageId: AFRES_AFBKDL_HBBUEXCH_VMTOOLS_NOT_INSTALL
//
// MessageText:
//
// The VMware Tools for virtual machine [%1!s!] is not installed or out of date. As a result, the subsequent Exchange catalog job will not be launched.%0
//
#define AFRES_AFBKDL_HBBUEXCH_VMTOOLS_NOT_INSTALL ((DWORD)0xC0080131L)

//
// MessageId: AFRES_AFBKDL_HBBUEXCH_EXCH_BINARY_NOT_PARSED
//
// MessageText:
//
// The Exchange binaries are not parsed successfully from virtual machine. As a result, the subsequent Exchange catalog job will fail.%0
//
#define AFRES_AFBKDL_HBBUEXCH_EXCH_BINARY_NOT_PARSED ((DWORD)0x80080132L)

//
// MessageId: AFRES_AFBKDL_CLUSTER_VOLUME_FOR_FULL_BACKUP
//
// MessageText:
//
// Windows Failover Cluster share disk status change detected since last successful backup. A full backup of volume %1!s! will be performed.%0
//
#define AFRES_AFBKDL_CLUSTER_VOLUME_FOR_FULL_BACKUP ((DWORD)0x80080133L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_EXPECTATION_FAIL_JOB
//
// MessageText:
//
// The backup job has been cancelled. For a VSS snapshot, the Hyper-V VSS writer needs to save the virtual machine and this is not applied in the current plan. To restart the backup job, you must change the Hyper-V Snapshot Method setting in the plan. For details on how to set Hyper-V Snapshot Method in a plan, see the product documentation.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_EXPECTATION_FAIL_JOB ((DWORD)0xC0080134L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_EXPECTATION_SAVE_VM
//
// MessageText:
//
// The virtual machine will be saved during the VSS snapshot creation as required by the Hyper-V VSS writer.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_EXPECTATION_SAVE_VM ((DWORD)0x80080135L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_EXPECTATION_IN_CONSISTENT
//
// MessageText:
//
// The backup job has prevented the Hyper-V VSS writer from saving the VM during snapshot creation. As a result, the backed up data may not be in a consistent state.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_EXPECTATION_IN_CONSISTENT ((DWORD)0x80080136L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_VSS_REPORT_FAILURE_IN_BACKUP
//
// MessageText:
//
// The Hyper-V VSS writer failed to process this VM because the VM is currently being backed up by another application.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_VSS_REPORT_FAILURE_IN_BACKUP ((DWORD)0xC0080137L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_DEPLOY_VM_IC_SERVICE_FAIL_HOST_NAME
//
// MessageText:
//
// ^AU_ProductName_HBVB_SHORT^ could not get the VM's host name, as a result, it cannot deploy the integration service to the VM. For more information about how to deploy the service manually, please check the product documentation.%0
//
#define AFRES_AFBKDL_HYPERV_VM_DEPLOY_VM_IC_SERVICE_FAIL_HOST_NAME ((DWORD)0x80080138L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_DEPLOY_VM_IC_SERVICE_FAIL_USERNAME
//
// MessageText:
//
// VM's username is not provided, as a result, ^AU_ProductName_HBVB_SHORT^ cannot deploy the integration service to the VM. For more information about how to deploy the service manually, please check the product documentation.%0
//
#define AFRES_AFBKDL_HYPERV_VM_DEPLOY_VM_IC_SERVICE_FAIL_USERNAME ((DWORD)0x80080139L)

//
// MessageId: AFRES_AFBKDL_VMWARE_CREATE_HW_SNAPSHOT
//
// MessageText:
//
// Attemping to create Hardware snapshot ...%0
//
#define AFRES_AFBKDL_VMWARE_CREATE_HW_SNAPSHOT ((DWORD)0x4008013AL)

//
// MessageId: AFRES_AFBKDL_VMWARE_DELETE_HW_SNAPSHOT
//
// MessageText:
//
// Attempting to delete Hardware snapshot ...%0
//
#define AFRES_AFBKDL_VMWARE_DELETE_HW_SNAPSHOT ((DWORD)0x4008013BL)

//
// MessageId: AFRES_AFBKDL_VMWARE_DISK_NOT_HW_SNAPSHOT
//
// MessageText:
//
// Hardware snapshot is not supported for virtual disk %1!s!. Please refer to the corresponding backend log file for more details.%0
//
#define AFRES_AFBKDL_VMWARE_DISK_NOT_HW_SNAPSHOT ((DWORD)0x8008013CL)

//
// MessageId: AFRES_AFBKDL_CBT_UPGRADED_INC
//
// MessageText:
//
// The Change Block Tracking (CBT) feature has been upgraded. As a result, redundant data may be backed up.%0
//
#define AFRES_AFBKDL_CBT_UPGRADED_INC    ((DWORD)0x8008013DL)

//
// MessageId: AFRES_AFBKDL_VMWARE_DISK_NOT_HW_SNAPSHOT_FALL_BACK
//
// MessageText:
//
// Hardware snapshot is not supported for virtual disk %1!s!. Please refer to the corresponding backend log file for more details. Will fall back to software snapshot.%0
// 
//
#define AFRES_AFBKDL_VMWARE_DISK_NOT_HW_SNAPSHOT_FALL_BACK ((DWORD)0x8008013EL)

//
// MessageId: AFRES_AFBKDL_VMWARE_DISK_NOT_HW_SNAPSHOT_FAIL_JOB
//
// MessageText:
//
// Hardware snapshot is not supported for one or more VMDK files residing on a HW applaince volume. Hence failing the backup job.%0
// 
//
#define AFRES_AFBKDL_VMWARE_DISK_NOT_HW_SNAPSHOT_FAIL_JOB ((DWORD)0xC008013FL)

//
// MessageId: AFRES_AFBKDL_VM_VSS_SNAPSHOT_METHOD
//
// MessageText:
//
// The backup job will use the "Microsoft VSS inside VM" snapshot method.%0
//
#define AFRES_AFBKDL_VM_VSS_SNAPSHOT_METHOD ((DWORD)0x40080140L)

//
// MessageId: AFRES_AFBKDL_VM_VMWARE_TOOLS_SNAPSHOT_METHOD
//
// MessageText:
//
// The backup job will use the "VMware Tools" snapshot method.%0
//
#define AFRES_AFBKDL_VM_VMWARE_TOOLS_SNAPSHOT_METHOD ((DWORD)0x40080141L)

//
// MessageId: AFRES_AFBKDL_VM_OFF_OR_OLD_TOOLS
//
// MessageText:
//
// Backup job unable deploy/undeploy tools for the "Microsoft VSS inside VM" snapshot method. The VMware Tools is not up to date.%0
//
#define AFRES_AFBKDL_VM_OFF_OR_OLD_TOOLS ((DWORD)0x80080142L)

//
// MessageId: AFRES_AFBKDL_VM_FAILED_TO_DEPLOY
//
// MessageText:
//
// Abort backup because backup job has been configured to use the "Microsoft VSS inside VM" snapshot method. However, only the "VMware Tools" snapshot method is applicable because ^AU_ProductName_HBVB_SHORT^ failed to deploy the necessary tools into the VM.%0
//
#define AFRES_AFBKDL_VM_FAILED_TO_DEPLOY ((DWORD)0xC0080143L)

//
// MessageId: AFRES_AFBKDL_VM_FAILED_TO_UNDEPLOY
//
// MessageText:
//
// Abort backup because backup job has been configured to use the "VMware Tools" snapshot method. However, only the "Microsoft VSS inside VM" snapshot method is applicable because ^AU_ProductName_HBVB_SHORT^ failed to undeploy tools from inside VM.%0
//
#define AFRES_AFBKDL_VM_FAILED_TO_UNDEPLOY ((DWORD)0xC0080144L)

//
// MessageId: AFRES_AFBKDL_VM_NON_WINDOWS_SNAPSHOT_METHOD
//
// MessageText:
//
// The VM is not a Windows machine. As a result, the backup job will use the "VMware Tools" snapshot method.%0
//
#define AFRES_AFBKDL_VM_NON_WINDOWS_SNAPSHOT_METHOD ((DWORD)0x40080145L)

//
// MessageId: AFRES_AFBKDL_VM_GUEST_OPERATION_FAILED
//
// MessageText:
//
// The VM guest operation failed. ESX/vCenter reports error: "%1!s!".%0
//
#define AFRES_AFBKDL_VM_GUEST_OPERATION_FAILED ((DWORD)0x80080146L)

//
// MessageId: AFRES_AFBKDL_VM_FAILURE_TO_DEPLOY
//
// MessageText:
//
// ^AU_ProductName_HBVB_SHORT^ failed to deploy the necessary tools for the "Microsoft VSS inside VM" snapshot method into the VM.%0
//
#define AFRES_AFBKDL_VM_FAILURE_TO_DEPLOY ((DWORD)0x80080147L)

//
// MessageId: AFRES_AFBKDL_VM_FAILURE_TO_UNDEPLOY
//
// MessageText:
//
// ^AU_ProductName_HBVB_SHORT^ failed to undeploy the tools for the "Microsoft VSS inside VM" snapshot method to enable the "VMware Tools" snapshot method from inside the VM.%0
//
#define AFRES_AFBKDL_VM_FAILURE_TO_UNDEPLOY ((DWORD)0x80080148L)

//
// MessageId: AFRES_AFBKDL_VM_FAILURE_TO_DETECT
//
// MessageText:
//
// ^AU_ProductName_HBVB_SHORT^ failed to detect the current snapshot method from inside the VM guest.%0
//
#define AFRES_AFBKDL_VM_FAILURE_TO_DETECT ((DWORD)0x80080149L)

//
// MessageId: AFRES_AFBKDL_VM_ESX4_NO_VIX
//
// MessageText:
//
// The ESX server version is 4.x; however, VIX of version 1.13.3 or later is not installed (but required) in the backup proxy machine. As a result, ^AU_ProductName_HBVB_SHORT^ cannot perform VM guest operations to configure the snapshot method from inside the VM.%0
//
#define AFRES_AFBKDL_VM_ESX4_NO_VIX      ((DWORD)0x8008014AL)

//
// MessageId: AFRES_AFBKDL_VM_EMMPTY_CRED
//
// MessageText:
//
// The guest VM credentials are empty. As a result, ^AU_ProductName_HBVB_SHORT^ cannot perform VM guest operations to configure the snapshot method from inside the VM. Please update the credentials via the console UI.%0
//
#define AFRES_AFBKDL_VM_EMMPTY_CRED      ((DWORD)0x8008014BL)

//
// MessageId: AFRES_AFBKDL_VM__NO_APP_VSS_SNAPSHOT_METHOD
//
// MessageText:
//
// Recovery point %1!d! cannot support an application restore because the "Microsoft VSS inside VM" snapshot method was used.%0
//
#define AFRES_AFBKDL_VM__NO_APP_VSS_SNAPSHOT_METHOD ((DWORD)0x8008014CL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_DISABLE_TRANSPORTABLE_FOR_CLUSTER_PROXY
//
// MessageText:
//
// The transportable snapshot option has been disabled because the proxy host [%1!s!] is a node within the target cluster.%0
//
#define AFRES_AFBKDL_HYPERV_VM_DISABLE_TRANSPORTABLE_FOR_CLUSTER_PROXY ((DWORD)0x8008014DL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_NON_WINDOWS
//
// MessageText:
//
// Recovery point check for node [%1!s!], session number [%2!d!] skipped. The guest OS is not Windows.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_NON_WINDOWS ((DWORD)0x8008014EL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_START
//
// MessageText:
//
// Recovery point check for node [%1!s!], session number [%2!d!] started.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_START ((DWORD)0x4008014FL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_SUCCEEDED
//
// MessageText:
//
// Recovery point check for node [%1!s!], session number [%2!d!] was successful.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_SUCCEEDED ((DWORD)0x40080150L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_FAILED
//
// MessageText:
//
// Recovery point check for node [%1!s!], session number [%2!d!] failed.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_FAILED ((DWORD)0xC0080151L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_FAILED_WITH_ERROR
//
// MessageText:
//
// Recovery point check for node [%1!s!], session number [%2!d!] failed with error [%3!d!].%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_FAILED_WITH_ERROR ((DWORD)0xC0080152L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_CANCELED
//
// MessageText:
//
// Recovery point check for node [%1!s!], session number [%2!d!] canceled.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_CANCELED ((DWORD)0x80080153L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_SKIPPED
//
// MessageText:
//
// Recovery point check result: Volume: [%1!s!], File System: [%2!s!], Volume Type: [%3!s!], Skipped.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_SKIPPED ((DWORD)0x80080154L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_OK
//
// MessageText:
//
// Recovery point check result: Volume: [%1!s!], File System: [%2!s!], Volume Type: [%3!s!], no problem found.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_OK ((DWORD)0x40080155L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_MOUNT
//
// MessageText:
//
// Recovery point check result: Volume: [%1!s!], File System: [%2!s!], Volume Type: [%3!s!], Error on mounting disk [%4!s!].%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_MOUNT ((DWORD)0xC0080156L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_CHKDSK
//
// MessageText:
//
// Recovery point check result: Volume: [%1!s!], File System: [%2!s!], Volume Type: [%3!s!], Error on checking disk [%4!s!].%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_CHKDSK ((DWORD)0xC0080157L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_DISMOUNT
//
// MessageText:
//
// Recovery point check result: Volume: [%1!s!], File System: [%2!s!], Volume Type: [%3!s!], Error on dismounting disk [%4!s!].%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_DISMOUNT ((DWORD)0xC0080158L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_UNKNOWN
//
// MessageText:
//
// Recovery point check result: Volume: [%1!s!], File System: [%2!s!], Volume Type: [%3!s!], Error [%4!s!].%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_DETAIL_PHASE_UNKNOWN ((DWORD)0xC0080159L)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_SIMPLE
//
// MessageText:
//
// Simple%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_SIMPLE ((DWORD)0x4008015AL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_SPAN
//
// MessageText:
//
// Spanned%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_SPAN ((DWORD)0x4008015BL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_STRIPE
//
// MessageText:
//
// Striped%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_STRIPE ((DWORD)0x4008015CL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_MIRROR
//
// MessageText:
//
// Mirrored%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_MIRROR ((DWORD)0x4008015DL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_PARITY
//
// MessageText:
//
// RAID-5%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_PARITY ((DWORD)0x4008015EL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_UNKNOWN
//
// MessageText:
//
// Unknown%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_VOLUME_TYPE_UNKNOWN ((DWORD)0x4008015FL)

//
// MessageId: AFRES_AFBKDL_HYPERV_SKIP_APP_METADATA_COLLECTING_CRASH_CONSISTENT
//
// MessageText:
//
// Collecting of application metadata skipped because the snapshot was not application consistent.%0
//
#define AFRES_AFBKDL_HYPERV_SKIP_APP_METADATA_COLLECTING_CRASH_CONSISTENT ((DWORD)0x80080160L)

//
// MessageId: AFRES_AFBKDL_NO_VOLUME_SELECTED
//
// MessageText:
//
// There is no volume selected in this backup job.%0
//
#define AFRES_AFBKDL_NO_VOLUME_SELECTED  ((DWORD)0xC0080161L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_DEPLOY_VM_IC_SERVICE_FAIL_UNKNOWN
//
// MessageText:
//
// Failed to deploy the integration service to the VM.%0
//
#define AFRES_AFBKDL_HYPERV_VM_DEPLOY_VM_IC_SERVICE_FAIL_UNKNOWN ((DWORD)0x80080162L)

//
// MessageId: AFRES_AFBKDL_TOTAL_THROUGHPUT_VMDK
//
// MessageText:
//
// The backup of %1!s! processed a total of %2!s! of source data in %3!s!, and the throughput was %4!s!/Min.%0
//
#define AFRES_AFBKDL_TOTAL_THROUGHPUT_VMDK ((DWORD)0x40080163L)

//
// MessageId: AFRES_AFBKDL_CANCEL_BACKUP_IF_ALL_DISKS_EXCLUDED
//
// MessageText:
//
// All virtual disks are excluded from the backup job. As a result, the backup job will be canceled.%0
//
#define AFRES_AFBKDL_CANCEL_BACKUP_IF_ALL_DISKS_EXCLUDED ((DWORD)0x80080164L)

//
// MessageId: AFRES_AFBKDL_AGENT_HW_SNAPSHOT_NOT_SUPPORT
//
// MessageText:
//
// Hardware snapshot is not supported for volume %1!s!.%0
//
#define AFRES_AFBKDL_AGENT_HW_SNAPSHOT_NOT_SUPPORT ((DWORD)0x80080165L)

//
// MessageId: AFRES_AFBKDL_AGENT_HW_SNAPSHOT_SUPPORT
//
// MessageText:
//
// Sutiatble Hardware provider found for volume %1!s!. Hardware snapshot is supported for volume %1!s!.%0
//
#define AFRES_AFBKDL_AGENT_HW_SNAPSHOT_SUPPORT ((DWORD)0x40080166L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_USE_TRANSPORTABLE
//
// MessageText:
//
// Backing up the VM using transportable snapshot.%0
//
#define AFRES_AFBKDL_HYPERV_VM_USE_TRANSPORTABLE ((DWORD)0x80080167L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_USE_HW_SNAPSHOT
//
// MessageText:
//
// Backing up the VM using non transportable hardware snapshot.%0
//
#define AFRES_AFBKDL_HYPERV_VM_USE_HW_SNAPSHOT ((DWORD)0x80080168L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_USE_SW_SNAPSHOT
//
// MessageText:
//
// Backing up the VM using software snapshot.%0
//
#define AFRES_AFBKDL_HYPERV_VM_USE_SW_SNAPSHOT ((DWORD)0x80080169L)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_DATA_ERROR
//
// MessageText:
//
// Data inconsistency found in recovery point %1!d!, the next job will be converted to a verify backup job.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_DATA_ERROR ((DWORD)0x8008016AL)

//
// MessageId: AFRES_AFBKDL_METHOD_CHANGE_DATA_ERROR_NEXT_JOB
//
// MessageText:
//
// Data inconsistency was found in the previous job, the job will be converted to a verify backup job.%0
//
#define AFRES_AFBKDL_METHOD_CHANGE_DATA_ERROR_NEXT_JOB ((DWORD)0x8008016BL)

//
// MessageId: AFRES_AFBKDL_NO_QUIESCING
//
// MessageText:
//
// The snapshot is taken without quiescing the file system in the virtual machine. As a result, the snapshot cannot represent a consistent state of the guest file systems. In case the virtual machine is powered off or VMware Tools are not available, the quiesce flag is ignored.%0
//
#define AFRES_AFBKDL_NO_QUIESCING        ((DWORD)0x8008016CL)

//
// MessageId: AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_CHDKSK_TIMEOUT
//
// MessageText:
//
// The chkdsk command was unable to complete the process within the expected time. The possible reason could be high system load. Please refer to the ^AU_ProductName_UDP_SHORT^ Solutions Guide for details.%0
//
#define AFRES_AFBKDL_VM_CHECK_RECOVERY_POINT_CHDKSK_TIMEOUT ((DWORD)0xC008016DL)

//
// MessageId: AFRES_AFBKDL_CBT_FAIL_RETRY_BACKUP
//
// MessageText:
//
// The changed block tracking (CBT) function failed to query the blocks to back up. As a result, ^AU_ProductName_HBVB_SHORT^ will reset the CBT and attempt to perform a backup again.%0
//
#define AFRES_AFBKDL_CBT_FAIL_RETRY_BACKUP ((DWORD)0x8008016EL)

//
// MessageId: AFRES_AFBKDL_VM_VOLUME_GENERATE_BITMAP_FAIL
//
// MessageText:
//
// The bitmap for file system %1!s!, volume %2!s! cannot be retrieved.%0
//
#define AFRES_AFBKDL_VM_VOLUME_GENERATE_BITMAP_FAIL ((DWORD)0x8008016FL)

//
// MessageId: AFRES_AFBKDL_VM_THROTTLLING_VALUE
//
// MessageText:
//
// The throughput limit is %1!s!/Min.%0
//
#define AFRES_AFBKDL_VM_THROTTLLING_VALUE ((DWORD)0x40080170L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_UPGRADE_CBT_MODULE_FAIL
//
// MessageText:
//
// Failed to upgrade The changed block tracking (CBT) function. System error=[%1!s!].%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_UPGRADE_CBT_MODULE_FAIL ((DWORD)0xC0080171L)

//
// MessageId: AFRES_AFBKDL_PHASE_TAKING_SW_SNAPSHOT
//
// MessageText:
//
// Taking software snapshot ...%0
//
#define AFRES_AFBKDL_PHASE_TAKING_SW_SNAPSHOT ((DWORD)0x40080172L)

//
// MessageId: AFRES_AFBKDL_PHASE_DELETING_SW_SNAPSHOT
//
// MessageText:
//
// Deleting software snapshot ...%0
//
#define AFRES_AFBKDL_PHASE_DELETING_SW_SNAPSHOT ((DWORD)0x40080173L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_PRECMD_VM
//
// MessageText:
//
// Executing the pre backup command in guest OS. (CMD=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_PRECMD_VM ((DWORD)0x40080174L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_POSTSNAPSHOTCMD_VM
//
// MessageText:
//
// Executing the post snapshot command in guest OS. (CMD=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_POSTSNAPSHOTCMD_VM ((DWORD)0x40080175L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_POSTCMD_VM
//
// MessageText:
//
// Executing the post backup command in guest OS. (CMD=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_POSTCMD_VM ((DWORD)0x40080176L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_FAIL_VM
//
// MessageText:
//
// Unable to execute command in guest OS. (EC=%1!s!)%0
//
#define AFRES_AFBKDL_PREPOSTCMD_FAIL_VM  ((DWORD)0xC0080177L)

//
// MessageId: AFRES_AFBKDL_PREPOSTCMD_FAILJOB_ON_EXIT_CODE_VM
//
// MessageText:
//
// Job Pre-Command in guest OS has exit code %1!d!.  Abort the job.%0
//
#define AFRES_AFBKDL_PREPOSTCMD_FAILJOB_ON_EXIT_CODE_VM ((DWORD)0x40080178L)

//
// MessageId: AFRES_AFBKDL_VM_THROTTLLING_NOLIMIT
//
// MessageText:
//
// The throughput limit is unset.%0
//
#define AFRES_AFBKDL_VM_THROTTLLING_NOLIMIT ((DWORD)0x40080179L)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_VSS_REPORT_FAILURE_CONTINUE
//
// MessageText:
//
// The Hyper-V VSS writer has encountered an error when processing this virtual machine. As a result, the backup data may not be consistent. (For more information about Hyper-V VSS writer errors, refer to the product documentation).%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_VSS_REPORT_FAILURE_CONTINUE ((DWORD)0x8008017AL)

//
// MessageId: AFRES_AFBKDL_VSS_METHOD_UPHEAVAL
//
// MessageText:
//
// The Incremental Backup converts to a Verify Backup because the previous backup contains inconsistent data.%0
//
#define AFRES_AFBKDL_VSS_METHOD_UPHEAVAL ((DWORD)0x8008017BL)

//
// MessageId: AFRES_AFBKND_TRANSPORT_SNAPSHOT_FAILED_FB_SOFTWARE
//
// MessageText:
//
// Tranpsortable Snapshot Failed. Falling back to Software Snapshot.%0
//
#define AFRES_AFBKND_TRANSPORT_SNAPSHOT_FAILED_FB_SOFTWARE ((DWORD)0x8008017CL)

//
// MessageId: AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_009
//
// MessageText:
//
// T?he credentials for the guest operating system is not provided. Pre/Post commands cannot be executed.%0
//
#define AFRES_AFBKDL_HYPERV_VM_BK_PREPOST_CMD_009 ((DWORD)0x8008017DL)

//
// MessageId: AFRES_AFBKDL_HANG_TERMINATED
//
// MessageText:
//
// The application with process Id %1!d! is timeout within %2!d! second(s), the process will be terminated.%0
//
#define AFRES_AFBKDL_HANG_TERMINATED     ((DWORD)0x8008017EL)

//
// MessageId: AFRES_AFBKDL_VM_ESX4_NO_VIX_PREPOST_CMD
//
// MessageText:
//
// The ESX server version is 4.x; however, VIX of version 1.13.3 or later is not installed (but required) in the backup proxy machine. As a result, ^AU_ProductName_HBVB_SHORT^ cannot perform VM guest operations to execute command inside the VM.%0
//
#define AFRES_AFBKDL_VM_ESX4_NO_VIX_PREPOST_CMD ((DWORD)0x8008017FL)

//
// MessageId: AFRES_AFBKDL_CBT_ABSENT
//
// MessageText:
//
// The Change Block Tracking (CBT) feature has been reset due to an upgrade or internal error. As a result, redundant data may be backed up.%0
//
#define AFRES_AFBKDL_CBT_ABSENT          ((DWORD)0x80080180L)

//
// MessageId: AFRES_AFBKDL_AGENT_HW_SNAPSHOT_NOT_SUPPORTED_VOL
//
// MessageText:
//
// Hardware Snapshot is not supported for few volumes. If a VSS Provider from a storage vendor is available, install and configure the VSS Provider to take advantage of Hardware Snapshot. For an updated list of supported Storage Arrays, see the Compatibility Matrix.%0
//
#define AFRES_AFBKDL_AGENT_HW_SNAPSHOT_NOT_SUPPORTED_VOL ((DWORD)0x40080181L)

//
// MessageId: AFRES_AFBKDL_AGENT_HW_SNAPSHOT_NOT_SUPPORTED_VDISK
//
// MessageText:
//
// Unable to process Hardware Snapshot for Virtual Disk %1!s!, switching to Software Snapshot. For debug information, see the %2!s! log.%0
//
#define AFRES_AFBKDL_AGENT_HW_SNAPSHOT_NOT_SUPPORTED_VDISK ((DWORD)0x80080182L)

//
// MessageId: AFRES_AFBKDL_VM_CRASH_SNAPSHOT
//
// MessageText:
//
// Taking snapshot without guest quiescence as it has failed to take quiescence snapshot.?%0
//
#define AFRES_AFBKDL_VM_CRASH_SNAPSHOT   ((DWORD)0x80080183L)

//
// MessageId: AFRES_AFBKDL_VM_FAILED_TO_DEPLOY_WARNING
//
// MessageText:
//
// Backup job is configured to use the "Microsoft VSS inside VM" snapshot method. However, only the "VMware Tools" snapshot method is applicable because ^AU_ProductName_HBVB_SHORT^ failed to deploy the necessary tools into the VM.%0
//
#define AFRES_AFBKDL_VM_FAILED_TO_DEPLOY_WARNING ((DWORD)0x80080184L)

//
// MessageId: AFRES_AFBKDL_VM_FAILED_TO_UNDEPLOY_WARNING
//
// MessageText:
//
// Backup job is configured to use the "VMware Tools" snapshot method. However, only the "Microsoft VSS inside VM" snapshot method is applicable because ^AU_ProductName_HBVB_SHORT^ failed to undeploy tools from VM.?%0
//
#define AFRES_AFBKDL_VM_FAILED_TO_UNDEPLOY_WARNING ((DWORD)0x80080185L)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_FLEX_CLONE_NOT_AVAILABE_NFS
//
// MessageText:
//
// A valid Flex Clone license was not found while creating HW snapshot of virtual disk %1!s!. Will attempt to use Microsoft NFS client if configured on the backup proxy, else will fall back to Software Snapshot.%0
//
#define AFRES_AFBKDL_VM_BACKUP_FLEX_CLONE_NOT_AVAILABE_NFS ((DWORD)0x80080186L)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_USE_NFS_CLIENT
//
// MessageText:
//
// Attempting to use Microsoft NFS Client to backup the virtual disk %1!s! from HW snapshot.%0
//
#define AFRES_AFBKDL_VM_BACKUP_USE_NFS_CLIENT ((DWORD)0x40080187L)

//
// MessageId: AFRES_AFBKDL_VSS_METHOD_USE_DIFFERENT_PROXY
//
// MessageText:
//
// The VM is being protected by another backup proxy, which is not allowed by the Change Block Tracking (CBT) function.%0
//
#define AFRES_AFBKDL_VSS_METHOD_USE_DIFFERENT_PROXY ((DWORD)0x80080188L)

//
// MessageId: AFRES_AFBKDL_VM_IS_BACKING_UP
//
// MessageText:
//
// The job failed to process this VM because the VM is currently being backed up by another application.%0
//
#define AFRES_AFBKDL_VM_IS_BACKING_UP    ((DWORD)0xC0080189L)

//
// MessageId: AFRES_AFBKDL_VM_BACKUP_FLEX_CLONE_NOT_AVAILABE_ISCSI
//
// MessageText:
//
// A valid Flex Clone license was not found while creating HW snapshot of virtual disk %1!s!. Attempting to use Snap Restore/LUN Clone to create HW snapshot.%0
//
#define AFRES_AFBKDL_VM_BACKUP_FLEX_CLONE_NOT_AVAILABE_ISCSI ((DWORD)0x8008018AL)

//
// MessageId: AFRES_AFBKDL_FAILED_TO_CREATE_SESSION
//
// MessageText:
//
// Failed to create new session on backup destination %1!s!. Verify if the backup destination is avaialble.%0
//
#define AFRES_AFBKDL_FAILED_TO_CREATE_SESSION ((DWORD)0xC008018BL)

//
// MessageId: AFRES_AFBKDL_VM_PREPOST_CMD_TIMEOUT
//
// MessageText:
//
// The command "%1!s!" has started in guest VM but cannot finish before timeout.%0
//
#define AFRES_AFBKDL_VM_PREPOST_CMD_TIMEOUT ((DWORD)0x8008018CL)

//
// MessageId: AFRES_AFBKDL_FLEX_CLONE_SNAP_RESTORE_NOT_PRESENT
//
// MessageText:
//
// A valid Flex Clone license and Snap Restore license was not found while creating HW snapshot of virtual disk %1!s!. Will fall back to Software Snapshot.%0
//
#define AFRES_AFBKDL_FLEX_CLONE_SNAP_RESTORE_NOT_PRESENT ((DWORD)0x8008018DL)

//
// MessageId: AFRES_AFBKDL_SNAP_RESTORE_DELETE_FAILED
//
// MessageText:
//
// Failed to delete the snapshot %1!s! on the storage appliance %1!s!. You can refer to the NETAPP plugin logs corresponding to this backup job for more details. Please delete the snapshot manually.%0
//
#define AFRES_AFBKDL_SNAP_RESTORE_DELETE_FAILED ((DWORD)0x8008018EL)

//
// MessageId: AFRES_AFBKDL_VM_VSS_METHOD_NA
//
// MessageText:
//
// The "Microsoft VSS inside VM" snapshot method is not available, instead the "VMware Tools" snapshot method will be used.%0
//
#define AFRES_AFBKDL_VM_VSS_METHOD_NA    ((DWORD)0x8008018FL)

//
// MessageId: AFRES_AFBKDL_VOLUME_NOT_HOSTED
//
// MessageText:
//
// Abort backup because volume [%1!s!] is not hosted on hard disks.%0
//
#define AFRES_AFBKDL_VOLUME_NOT_HOSTED   ((DWORD)0xC0080190L)

//
// MessageId: AFRES_AFBKDL_HW_APPLIANCE_MISSING_SINGLE
//
// MessageText:
//
// Virtual Disks residing on datastore  %1!s! would be backed up using Software Snapshot, please configure storage arrays in UDP Console to take advantage of Hardware Snapshot. For an updated list of supported StorageArrays, see the Compatibility Matrix.%0
//
#define AFRES_AFBKDL_HW_APPLIANCE_MISSING_SINGLE ((DWORD)0x80080191L)

//
// MessageId: AFRES_AFBKDL_HW_APPLIANCE_MISSING_MULTIPLE
//
// MessageText:
//
// Virtual Disks residing on datastores  %1!s! would be backed up using Software Snapshot, please configure storage arrays in UDP Console to take advantage of Hardware Snapshot. For an updated list of supported Storage Arrays, see the Compatibility Matrix.%0
//
#define AFRES_AFBKDL_HW_APPLIANCE_MISSING_MULTIPLE ((DWORD)0x80080192L)

//
// MessageId: AFRES_AFBKDL_HW_APPLIANCE_MISSING_UNKNOWN_SINGLE
//
// MessageText:
//
// Virtual Disks residing on datastore  %1!s! would be backed up using Software Snapshot, please configure storage arrays in UDP Console to take advantage of Hardware Snapshot. For an updated list of supported Storage Arrays, see the Compatibility Matrix.%0
//
#define AFRES_AFBKDL_HW_APPLIANCE_MISSING_UNKNOWN_SINGLE ((DWORD)0x40080193L)

//
// MessageId: AFRES_AFBKDL_HW_APPLIANCE_MISSING_UNKNOWN_MULTIPLE
//
// MessageText:
//
// Virtual Disks residing on datastores  %1!s! would be backed up using Software Snapshot, please configure storage arrays in UDP Console to take advantage of Hardware Snapshot. For an updated list of supported Storage Arrays, see the Compatibility Matrix.%0
//
#define AFRES_AFBKDL_HW_APPLIANCE_MISSING_UNKNOWN_MULTIPLE ((DWORD)0x40080194L)

 // The following are the message definitions for Catalog
//
// MessageId: AFRES_OFFCAT_CATALOG_PROCESS_START
//
// MessageText:
//
// Start to generate catalog. Job id is: %1!u!.%0
//
#define AFRES_OFFCAT_CATALOG_PROCESS_START ((DWORD)0x40090001L)

//
// MessageId: AFRES_OFFCAT_PARSE_JOB_SCRIPT_SUCCEED
//
// MessageText:
//
// Parse process of job script successful.%0
//
#define AFRES_OFFCAT_PARSE_JOB_SCRIPT_SUCCEED ((DWORD)0x40090002L)

//
// MessageId: AFRES_OFFCAT_PARSE_JOB_SCRIPT_FAILED
//
// MessageText:
//
// Failed to parse job script.%0
//
#define AFRES_OFFCAT_PARSE_JOB_SCRIPT_FAILED ((DWORD)0xC0090003L)

//
// MessageId: AFRES_OFFCAT_NO_JOB_SCRIPT_TO_BE_RUN
//
// MessageText:
//
// Failed to run job script for generating catalog because there is no more job script in job queue.%0
//
#define AFRES_OFFCAT_NO_JOB_SCRIPT_TO_BE_RUN ((DWORD)0xC0090004L)

//
// MessageId: AFRES_OFFCAT_JOB_SCRIPT_IS_INVALID
//
// MessageText:
//
// Current job script is invalid. (Session is invalid)%0
//
#define AFRES_OFFCAT_JOB_SCRIPT_IS_INVALID ((DWORD)0x80090005L)

//
// MessageId: AFRES_OFFCAT_INIT_BACKUP_DEST_SUCCEED_WITH_USERNAME
//
// MessageText:
//
// Initialization of backup destination successful. (Destination=[%1!s!], UserName=[%2!s!])%0
//
#define AFRES_OFFCAT_INIT_BACKUP_DEST_SUCCEED_WITH_USERNAME ((DWORD)0x40090006L)

//
// MessageId: AFRES_OFFCAT_INIT_BACKUP_DEST_FAILED_WITH_USERNAME
//
// MessageText:
//
// Failed to initialize backup destination. (Destination=[%1!s!], UserName=[%2!s!])%0
//
#define AFRES_OFFCAT_INIT_BACKUP_DEST_FAILED_WITH_USERNAME ((DWORD)0xC0090007L)

//
// MessageId: AFRES_OFFCAT_GEN_FS_CATALOG_START
//
// MessageText:
//
// Start to generate catalog for file system.%0
//
#define AFRES_OFFCAT_GEN_FS_CATALOG_START ((DWORD)0x40090008L)

//
// MessageId: AFRES_OFFCAT_GEN_EXCH_GRT_CATALOG_START
//
// MessageText:
//
// Start to generate catalog for Exchange Granular Restore.%0
//
#define AFRES_OFFCAT_GEN_EXCH_GRT_CATALOG_START ((DWORD)0x40090009L)

//
// MessageId: AFRES_OFFCAT_FS_CATALOG_INFORMATION
//
// MessageText:
//
// Session information: Session Number=[%1!u!], Job ID=[%2!u!], Backup Time=[%3!s!], Backup Name=[%4!s!].%0
//
#define AFRES_OFFCAT_FS_CATALOG_INFORMATION ((DWORD)0x4009000AL)

//
// MessageId: AFRES_OFFCAT_EXCH_GRT_CATALOG_INFO
//
// MessageText:
//
// Session information: Session Number=[%1!u!], Sub-Session Number=[%2!u!].%0
//
#define AFRES_OFFCAT_EXCH_GRT_CATALOG_INFO ((DWORD)0x4009000BL)

//
// MessageId: AFRES_OFFCAT_REPAIR_LAST_FAILED_SESS_START
//
// MessageText:
//
// Start to verify existing sessions.%0
//
#define AFRES_OFFCAT_REPAIR_LAST_FAILED_SESS_START ((DWORD)0x4009000CL)

//
// MessageId: AFRES_OFFCAT_REPAIR_LAST_FAILED_SESS_SUCCEED
//
// MessageText:
//
// Purge of failed session successful.%0
//
#define AFRES_OFFCAT_REPAIR_LAST_FAILED_SESS_SUCCEED ((DWORD)0x4009000DL)

//
// MessageId: AFRES_OFFCAT_REPAIR_LAST_FAILED_SESS_FAILED
//
// MessageText:
//
// Failed to verify purge failed session.%0
//
#define AFRES_OFFCAT_REPAIR_LAST_FAILED_SESS_FAILED ((DWORD)0x4009000EL)

//
// MessageId: AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_START
//
// MessageText:
//
// Start to verify merged sessions.%0
//
#define AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_START ((DWORD)0x4009000FL)

//
// MessageId: AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_SUCCEED
//
// MessageText:
//
// Merge of failed session successful.%0
//
#define AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_SUCCEED ((DWORD)0x40090010L)

//
// MessageId: AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_FAILED
//
// MessageText:
//
// Failed to merge failed session.%0
//
#define AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_FAILED ((DWORD)0xC0090011L)

//
// MessageId: AFRES_OFFCAT_MERGE_SESS_START
//
// MessageText:
//
// Start to verify the number of recovery points.%0
//
#define AFRES_OFFCAT_MERGE_SESS_START    ((DWORD)0x40090012L)

//
// MessageId: AFRES_OFFCAT_MERGE_SESS_SUCCEED
//
// MessageText:
//
// Merge of sessions successful.%0
//
#define AFRES_OFFCAT_MERGE_SESS_SUCCEED  ((DWORD)0x40090013L)

//
// MessageId: AFRES_OFFCAT_MERGE_SESS_FAILED
//
// MessageText:
//
// Failed to merge sessions.%0
//
#define AFRES_OFFCAT_MERGE_SESS_FAILED   ((DWORD)0xC0090014L)

//
// MessageId: AFRES_OFFCAT_MERGE_SINGLE_SESSION_START
//
// MessageText:
//
// Start to merge sessions %1!u! and %2!u!.%0
//
#define AFRES_OFFCAT_MERGE_SINGLE_SESSION_START ((DWORD)0x40090015L)

//
// MessageId: AFRES_OFFCAT_MERGE_SINGLE_SESSION_SUCCEED
//
// MessageText:
//
// Merge of sessions %1!u! and %2!u! successful.%0
//
#define AFRES_OFFCAT_MERGE_SINGLE_SESSION_SUCCEED ((DWORD)0x40090016L)

//
// MessageId: AFRES_OFFCAT_MERGE_SINGLE_SESSION_FAILED
//
// MessageText:
//
// Failed to merge sessions %1!u! and %2!u!.%0
//
#define AFRES_OFFCAT_MERGE_SINGLE_SESSION_FAILED ((DWORD)0xC0090017L)

//
// MessageId: AFRES_OFFCAT_GEN_CATALOG_FILE_FOR_VOLUME_SUCCEED
//
// MessageText:
//
// Generation of catalog file for volume %1!s! successful.%0
//
#define AFRES_OFFCAT_GEN_CATALOG_FILE_FOR_VOLUME_SUCCEED ((DWORD)0x40090018L)

//
// MessageId: AFRES_OFFCAT_GEN_CATALOG_FILE_FOR_VOLUME_FAILED
//
// MessageText:
//
// Generate catalog file failed because internal error. Please check backup log to confirm if data is in a consistent state. (Volume=[%1!s!], Backup Job ID=[%2!u!])%0
//
#define AFRES_OFFCAT_GEN_CATALOG_FILE_FOR_VOLUME_FAILED ((DWORD)0xC0090019L)

//
// MessageId: AFRES_OFFCAT_GEN_INDEX_FILE_FOR_VOLUME_SUCCEED
//
// MessageText:
//
// Generation of index file for volume %1!s! successful.%0
//
#define AFRES_OFFCAT_GEN_INDEX_FILE_FOR_VOLUME_SUCCEED ((DWORD)0x4009001AL)

//
// MessageId: AFRES_OFFCAT_GEN_INDEX_FILE_FOR_VOLUME_FAILED
//
// MessageText:
//
// Failed to generate index file for volume %1!s!.%0
//
#define AFRES_OFFCAT_GEN_INDEX_FILE_FOR_VOLUME_FAILED ((DWORD)0xC009001BL)

//
// MessageId: AFRES_OFFCAT_MOVE_CATALOG_FILE_TO_SESS_FOLDER_SUCEED
//
// MessageText:
//
// Move of catalog file to session folder successful.%0
//
#define AFRES_OFFCAT_MOVE_CATALOG_FILE_TO_SESS_FOLDER_SUCEED ((DWORD)0x4009001CL)

//
// MessageId: AFRES_OFFCAT_MOVE_CATALOG_FILE_TO_SESS_FOLDER_FAILED
//
// MessageText:
//
// Failed to move catalog file to session folder.%0
//
#define AFRES_OFFCAT_MOVE_CATALOG_FILE_TO_SESS_FOLDER_FAILED ((DWORD)0xC009001DL)

//
// MessageId: AFRES_OFFCAT_UPDATE_SESS_INFO_SUCCEED
//
// MessageText:
//
// Update of session information successful.%0
//
#define AFRES_OFFCAT_UPDATE_SESS_INFO_SUCCEED ((DWORD)0x4009001EL)

//
// MessageId: AFRES_OFFCAT_UPDATE_SESS_INFO_FAILED
//
// MessageText:
//
// Failed to update session information.%0
//
#define AFRES_OFFCAT_UPDATE_SESS_INFO_FAILED ((DWORD)0xC009001FL)

//
// MessageId: AFRES_OFFCAT_UPDATE_CLUSTER_MAP_SUCCEED
//
// MessageText:
//
// Update of cluster map information successful.%0
//
#define AFRES_OFFCAT_UPDATE_CLUSTER_MAP_SUCCEED ((DWORD)0x40090020L)

//
// MessageId: AFRES_OFFCAT_UPDATE_CLUSTER_MAP_FAILED
//
// MessageText:
//
// Failed to update cluster map information.%0
//
#define AFRES_OFFCAT_UPDATE_CLUSTER_MAP_FAILED ((DWORD)0xC0090021L)

//
// MessageId: AFRES_OFFCAT_CATALOG_PROCESS_SUCCEED
//
// MessageText:
//
// Catalog generation process successful.%0
//
#define AFRES_OFFCAT_CATALOG_PROCESS_SUCCEED ((DWORD)0x40090022L)

//
// MessageId: AFRES_OFFCAT_CATALOG_PROCESS_FAILED
//
// MessageText:
//
// Failed to generate catalog.%0
//
#define AFRES_OFFCAT_CATALOG_PROCESS_FAILED ((DWORD)0xC0090023L)

//
// MessageId: AFRES_OFFCAT_SKIP_MERGE_WHEN_NOT_ALLOWED
//
// MessageText:
//
// Merge session is skipped because the session is locked by another operation. Verify if any recovery points are mounted and dismount them.%0
//
#define AFRES_OFFCAT_SKIP_MERGE_WHEN_NOT_ALLOWED ((DWORD)0x80090024L)

//
// MessageId: AFRES_OFFCAT_MERGE_SINGLE_SESSION
//
// MessageText:
//
// Session %1!u! is merged. (Retention Count=[%2!u!])%0
//
#define AFRES_OFFCAT_MERGE_SINGLE_SESSION ((DWORD)0x40090025L)

//
// MessageId: AFRES_OFFCAT_MERGE_SESSION_SUMMARY
//
// MessageText:
//
// Total %1!u! sessions are merged. (Retention Count=[%2!u!])%0
//
#define AFRES_OFFCAT_MERGE_SESSION_SUMMARY ((DWORD)0x40090026L)

//
// MessageId: AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_SKIPPED
//
// MessageText:
//
// Merge of failed sessions is skipped. Maybe these sessions have been purged.%0
//
#define AFRES_OFFCAT_REPAIR_FAILED_MERGED_SESS_SKIPPED ((DWORD)0x80090027L)

//
// MessageId: AFRES_OFFCAT_MERGE_SESS_SKIPPED
//
// MessageText:
//
// Merge of sessions is skipped. Maybe these session have been purged.%0
//
#define AFRES_OFFCAT_MERGE_SESS_SKIPPED  ((DWORD)0x80090028L)

//
// MessageId: AFRES_OFFCAT_CATALOG_JOB_IS_SKIPPED
//
// MessageText:
//
// Catalog job is skipped because session has been purged.%0
//
#define AFRES_OFFCAT_CATALOG_JOB_IS_SKIPPED ((DWORD)0x80090029L)

//
// MessageId: AFRES_OFFCAT_PICK_NEW_VALID_JOB_SCRIPT
//
// MessageText:
//
// Pick one new valid job script for catalog.%0
//
#define AFRES_OFFCAT_PICK_NEW_VALID_JOB_SCRIPT ((DWORD)0x4009002AL)

//
// MessageId: AFRES_OFFCAT_INVALID_INDEX_SIG_4_VOLUME_NEED_CHKDSK
//
// MessageText:
//
// Failed to generate catalog file for volume %1!s! because of something wrong with data. To solve this problem please submit a full backup after performing chkdsk command.%0
//
#define AFRES_OFFCAT_INVALID_INDEX_SIG_4_VOLUME_NEED_CHKDSK ((DWORD)0xC009002BL)

//
// MessageId: AFRES_OFFCAT_SKIP_REFS_VOLUME
//
// MessageText:
//
// Generation of the catalog file for ReFS volume %1!s! is skipped.%0
//
#define AFRES_OFFCAT_SKIP_REFS_VOLUME    ((DWORD)0x8009002CL)

//
// MessageId: AFRES_OFFCAT_SKIP_DEDUPE_VOLUME
//
// MessageText:
//
// Generation of the catalog file for Deduplication-Enabled volume %1!s! is skipped.%0
//
#define AFRES_OFFCAT_SKIP_DEDUPE_VOLUME  ((DWORD)0x8009002DL)

//
// MessageId: AFRES_OFFCAT_SKIP_DEDUPE_VOLUME_FOR_FILE_COPY
//
// MessageText:
//
// Volume %1!s! is Deduplication-Enabled volume, file copy will skip this volume.%0
//
#define AFRES_OFFCAT_SKIP_DEDUPE_VOLUME_FOR_FILE_COPY ((DWORD)0x8009002EL)

//
// MessageId: AFRES_OFFCAT_DISABLE_FILE_COPY_FOR_CHECK_VOLUME_ERROR
//
// MessageText:
//
// Failed to check volumes %1!s! to determine if the volumes are a file copy source. File copy will be disabled.%0
//
#define AFRES_OFFCAT_DISABLE_FILE_COPY_FOR_CHECK_VOLUME_ERROR ((DWORD)0x8009002FL)

//
// MessageId: AFRES_OFFCAT_SAVE_SCRIPT_FAILED_SYS_ERRMSG
//
// MessageText:
//
// Failed to save catalog job script to %1!s!. (%2!s!)%0
//
#define AFRES_OFFCAT_SAVE_SCRIPT_FAILED_SYS_ERRMSG ((DWORD)0xC0090030L)

//
// MessageId: AFRES_OFFCAT_SKIP_REFS_VOLUME_FOR_FILE_COPY
//
// MessageText:
//
// Volume %1!s! is ReFS volume, File Copy will skip this volume.%0
//
#define AFRES_OFFCAT_SKIP_REFS_VOLUME_FOR_FILE_COPY ((DWORD)0x80090031L)

//
// MessageId: AFRES_OFFCAT_JOB_IS_CANCELLED_BY_USER
//
// MessageText:
//
// Catalog job canceled.%0
//
#define AFRES_OFFCAT_JOB_IS_CANCELLED_BY_USER ((DWORD)0x80090032L)

//
// MessageId: AFRES_OFFCAT_INIT_BACKUP_DEST_SUCCEED
//
// MessageText:
//
// Initialization of backup destination successful. (Destination=[%1!s!])%0
//
#define AFRES_OFFCAT_INIT_BACKUP_DEST_SUCCEED ((DWORD)0x40090033L)

//
// MessageId: AFRES_OFFCAT_INIT_BACKUP_DEST_FAILED
//
// MessageText:
//
// Failed to initialize backup destination. (Destination=[%1!s!])%0
//
#define AFRES_OFFCAT_INIT_BACKUP_DEST_FAILED ((DWORD)0xC0090034L)

//
// MessageId: AFRES_OFFCAT_VMIMAGE_OPEN_DISK_ERROR
//
// MessageText:
//
// Open virtual disk failed. (Volume=[%1!s!], EC=[%2!u!])%0
//
#define AFRES_OFFCAT_VMIMAGE_OPEN_DISK_ERROR ((DWORD)0xC0090035L)

//
// MessageId: AFRES_OFFCAT_VMIMAGE_READ_DATA_ERROR
//
// MessageText:
//
// Read data from virtual disk failed. (Volume=[%1!s!], EC=[%2!u!])%0
//
#define AFRES_OFFCAT_VMIMAGE_READ_DATA_ERROR ((DWORD)0xC0090036L)

//
// MessageId: AFRES_OFFCAT_VMIMAGE_SEEK_DATA_ERROR
//
// MessageText:
//
// Seek data on virtual disk failed. (Volume=[%1!s!], EC=[%2!u!])%0
//
#define AFRES_OFFCAT_VMIMAGE_SEEK_DATA_ERROR ((DWORD)0xC0090037L)

//
// MessageId: AFRES_OFFCAT_MEMORY_ALLOCATE_ERROR
//
// MessageText:
//
// Generate index file failed, cause available memory is not enough. Currently in used memory is [%1!u!MB], need [%2!u!MB] free memory for this operation.%0
//
#define AFRES_OFFCAT_MEMORY_ALLOCATE_ERROR ((DWORD)0xC0090038L)

//
// MessageId: AFRES_OFFCAT_MEMORY_TOOSMALL_ERROR
//
// MessageText:
//
// Generate index file failed, cause physical memory is too small. Currently in used memory is [%1!u!MB], recommend to enlarge memory to at least [%2!u!MB].%0
//
#define AFRES_OFFCAT_MEMORY_TOOSMALL_ERROR ((DWORD)0xC0090039L)

//
// MessageId: AFRES_OFFCAT_BACKUP_DEST_UNDER_DELETION
//
// MessageText:
//
// Catalog job failed because of backup destination is under deleting by purge job.%0
//
#define AFRES_OFFCAT_BACKUP_DEST_UNDER_DELETION ((DWORD)0xC009003AL)

//
// MessageId: AFRES_OFFCAT_BACKUP_SKIPPED_FILES_DIRS
//
// MessageText:
//
// Refer the log file [%1!s!] to know the list of files that were skipped in catalog.%0
//
#define AFRES_OFFCAT_BACKUP_SKIPPED_FILES_DIRS ((DWORD)0x8009003BL)

 // The following are the message definitions for CommLib
//
// MessageId: IDS_COMMLIB_XONET_OK
//
// MessageText:
//
// The operation completed successfully.%0
//
#define IDS_COMMLIB_XONET_OK             ((DWORD)0xC00A0001L)

//
// MessageId: IDS_COMMLIB_XONET_UNKNOWN
//
// MessageText:
//
// An unknown error occurred.%0
//
#define IDS_COMMLIB_XONET_UNKNOWN        ((DWORD)0xC00A0002L)

//
// MessageId: IDS_COMMLIB_XONET_CORE_ERROR
//
// MessageText:
//
// An unknown server error occurred.%0
//
#define IDS_COMMLIB_XONET_CORE_ERROR     ((DWORD)0xC00A0003L)

//
// MessageId: IDS_COMMLIB_XONET_BAD_APP_PARAM
//
// MessageText:
//
// Bad application parameters.%0
//
#define IDS_COMMLIB_XONET_BAD_APP_PARAM  ((DWORD)0xC00A0004L)

//
// MessageId: IDS_COMMLIB_XONET_LOST_ACK
//
// MessageText:
//
// Lost acknowledgement.%0
//
#define IDS_COMMLIB_XONET_LOST_ACK       ((DWORD)0xC00A0005L)

//
// MessageId: IDS_COMMLIB_XONET_CNT_BROKEN
//
// MessageText:
//
// Connection broken.%0
//
#define IDS_COMMLIB_XONET_CNT_BROKEN     ((DWORD)0xC00A0006L)

//
// MessageId: IDS_COMMLIB_XONET_CNT_CLOSED_BY_PEER
//
// MessageText:
//
// Connection to remote server is closed.%0
//
#define IDS_COMMLIB_XONET_CNT_CLOSED_BY_PEER ((DWORD)0xC00A0007L)

//
// MessageId: IDS_COMMLIB_XONET_TIMEOUT
//
// MessageText:
//
// Connection timeout.%0
//
#define IDS_COMMLIB_XONET_TIMEOUT        ((DWORD)0xC00A0008L)

//
// MessageId: IDS_COMMLIB_XONET_BAD_SOCKET
//
// MessageText:
//
// Invalid socket.%0
//
#define IDS_COMMLIB_XONET_BAD_SOCKET     ((DWORD)0xC00A0009L)

//
// MessageId: IDS_COMMLIB_XONET_WOULD_BLOCK
//
// MessageText:
//
// A non-blocking socket operation could not be completed immediately.%0
//
#define IDS_COMMLIB_XONET_WOULD_BLOCK    ((DWORD)0xC00A000AL)

//
// MessageId: IDS_COMMLIB_XONET_NO_MORE_SOCKETS
//
// MessageText:
//
// Too many open sockets.%0
//
#define IDS_COMMLIB_XONET_NO_MORE_SOCKETS ((DWORD)0xC00A000BL)

//
// MessageId: IDS_COMMLIB_XONET_NETWORK_DOWN
//
// MessageText:
//
// A socket operation encountered a dead network.%0
//
#define IDS_COMMLIB_XONET_NETWORK_DOWN   ((DWORD)0xC00A000CL)

//
// MessageId: IDS_COMMLIB_XONET_NO_MORE_BUFS
//
// MessageText:
//
// An operation on a socket could not be performed because the system lacked sufficient buffer space or because a queue was full.%0
//
#define IDS_COMMLIB_XONET_NO_MORE_BUFS   ((DWORD)0xC00A000DL)

//
// MessageId: IDS_COMMLIB_XONET_CNT_REFUSED_BY_OS
//
// MessageText:
//
// No connection could be made because the target machine actively refused it.%0
//
#define IDS_COMMLIB_XONET_CNT_REFUSED_BY_OS ((DWORD)0xC00A000EL)

//
// MessageId: IDS_COMMLIB_XONET_CNT_RESET
//
// MessageText:
//
// An existing connection was forcibly closed by the remote host.%0
//
#define IDS_COMMLIB_XONET_CNT_RESET      ((DWORD)0xC00A000FL)

//
// MessageId: IDS_COMMLIB_XONET_SYSTEM_TIMEOUT
//
// MessageText:
//
// A connection attempt failed because the connected party did not properly respond after a period of time.%0
//
#define IDS_COMMLIB_XONET_SYSTEM_TIMEOUT ((DWORD)0xC00A0010L)

//
// MessageId: IDS_COMMLIB_XONET_CNT_SHUT_DOWN
//
// MessageText:
//
// A request to send or receive data was disallowed because the socket had already been shut down in that direction with a previous shutdown call.%0
//
#define IDS_COMMLIB_XONET_CNT_SHUT_DOWN  ((DWORD)0xC00A0011L)

//
// MessageId: IDS_COMMLIB_XONET_ROUTING_PROBLEM
//
// MessageText:
//
// A socket operation was attempted to an unreachable network.%0
//
#define IDS_COMMLIB_XONET_ROUTING_PROBLEM ((DWORD)0xC00A0012L)

//
// MessageId: IDS_COMMLIB_XONET_UNKNOWN_ADDRESS
//
// MessageText:
//
// The requested address is not valid in its context.%0
//
#define IDS_COMMLIB_XONET_UNKNOWN_ADDRESS ((DWORD)0xC00A0013L)

//
// MessageId: IDS_COMMLIB_XONET_INIT_SSL_ERROR
//
// MessageText:
//
// Failed to initialize SSL.%0
//
#define IDS_COMMLIB_XONET_INIT_SSL_ERROR ((DWORD)0xC00A0014L)

//
// MessageId: IDS_COMMLIB_XONET_IO_ERROR
//
// MessageText:
//
// Failed to send data via SSL.%0
//
#define IDS_COMMLIB_XONET_IO_ERROR       ((DWORD)0xC00A0015L)

//
// MessageId: IDS_COMMLIB_XONET_BAD_PARAMETER
//
// MessageText:
//
// The parameter is incorrect.%0
//
#define IDS_COMMLIB_XONET_BAD_PARAMETER  ((DWORD)0xC00A0016L)

//
// MessageId: IDS_COMMLIB_XONET_HANDSHAKE_ERROR
//
// MessageText:
//
// Connection handshake error occurred.%0
//
#define IDS_COMMLIB_XONET_HANDSHAKE_ERROR ((DWORD)0xC00A0017L)

//
// MessageId: IDS_COMMLIB_PROXY_SERVER_CONN_ERR
//
// MessageText:
//
// Attempt to connect the proxy server failed. Please make sure the proxy configuration is correct and can connect to the destination server.%0
//
#define IDS_COMMLIB_PROXY_SERVER_CONN_ERR ((DWORD)0xC00A0018L)

//
// MessageId: IDS_COMMLIB_PROXY_AUTHEN_ERR
//
// MessageText:
//
// The proxy server requires user credential to authenticate.%0
//
#define IDS_COMMLIB_PROXY_AUTHEN_ERR     ((DWORD)0xC00A0019L)

//
// MessageId: IDS_COMMLIB_CONFIG_SYNC
//
// MessageText:
//
// The configuration file format is incorrect.%0
//
#define IDS_COMMLIB_CONFIG_SYNC          ((DWORD)0xC00A001AL)

//
// MessageId: IDS_COMMLIB_TRANST_SYNC
//
// MessageText:
//
// The transport type parameter is incorrect.%0
//
#define IDS_COMMLIB_TRANST_SYNC          ((DWORD)0xC00A001BL)

//
// MessageId: IDS_COMMLIB_FILE_TYPE_SYNC
//
// MessageText:
//
// The file type is incorrect.%0
//
#define IDS_COMMLIB_FILE_TYPE_SYNC       ((DWORD)0xC00A001CL)

//
// MessageId: IDS_COMMLIB_MEM_ALLOC_SYNC
//
// MessageText:
//
// The system cannot allocate the needed memory.%0
//
#define IDS_COMMLIB_MEM_ALLOC_SYNC       ((DWORD)0xC00A001DL)

//
// MessageId: IDS_COMMLIB_FILE_INTERFACE_SYNC
//
// MessageText:
//
// Unable to load the file interface.%0
//
#define IDS_COMMLIB_FILE_INTERFACE_SYNC  ((DWORD)0xC00A001EL)

//
// MessageId: IDS_COMMLIB_CONNECT_TIME_OUT
//
// MessageText:
//
// An attempt to connect the server timed out without establishing a connection.%0
//
#define IDS_COMMLIB_CONNECT_TIME_OUT     ((DWORD)0xC00A001FL)

//
// MessageId: IDS_COMMLIB_FILE_SIZE_SYNC
//
// MessageText:
//
// The target file size is larger than source size.%0
//
#define IDS_COMMLIB_FILE_SIZE_SYNC       ((DWORD)0xC00A0020L)

//
// MessageId: IDS_COMMLIB_CTX_SYNC
//
// MessageText:
//
// The client context or the server context is incorrect.%0
//
#define IDS_COMMLIB_CTX_SYNC             ((DWORD)0xC00A0021L)

//
// MessageId: IDS_COMMLIB_OUT_SYQU_SYNC
//
// MessageText:
//
// The received packet is out of sequence.%0
//
#define IDS_COMMLIB_OUT_SYQU_SYNC        ((DWORD)0xC00A0022L)

//
// MessageId: IDS_COMMLIB_TIME_OUT_SYNC
//
// MessageText:
//
// The asynchronous sending has not get the expected reply from the server within the timeout period.%0
//
#define IDS_COMMLIB_TIME_OUT_SYNC        ((DWORD)0xC00A0023L)

//
// MessageId: IDS_COMMLIB_INTERNAL_ERR_SYNC
//
// MessageText:
//
// An existing connection was forcibly closed by the remote host.%0
//
#define IDS_COMMLIB_INTERNAL_ERR_SYNC    ((DWORD)0xC00A0024L)

//
// MessageId: IDS_COMMLIB_GDD_CFG_FILE_TOO_LARGE
//
// MessageText:
//
// The size of GDD configure file is too large. The limit is 4MB.%0
//
#define IDS_COMMLIB_GDD_CFG_FILE_TOO_LARGE ((DWORD)0xC00A0025L)

//
// MessageId: IDS_COMMLIB_ERR_FILE_CMD_SYNC
//
// MessageText:
//
// An invalid file command was caught.%0
//
#define IDS_COMMLIB_ERR_FILE_CMD_SYNC    ((DWORD)0xC00A0026L)

//
// MessageId: IDS_COMMLIB_SERVER_UNVAILABLE
//
// MessageText:
//
// The server is down or not available.%0
//
#define IDS_COMMLIB_SERVER_UNVAILABLE    ((DWORD)0xC00A0027L)

//
// MessageId: IDS_COMMLIB_TARGET_SIZE_ERR_SYNC
//
// MessageText:
//
// The target file size is larger than the source file size.%0
//
#define IDS_COMMLIB_TARGET_SIZE_ERR_SYNC ((DWORD)0xC00A0028L)

//
// MessageId: IDS_COMMLIB_ASYNC_ERR_SYNC
//
// MessageText:
//
// The asynchronous sending method has encountered the internal error.%0
//
#define IDS_COMMLIB_ASYNC_ERR_SYNC       ((DWORD)0xC00A0029L)

//
// MessageId: IDS_COMMLIB_ASYNC_TIME_OUT_SYNC
//
// MessageText:
//
// The asynchronous sending has not get the expected reply from the server within the timeout period.%0
//
#define IDS_COMMLIB_ASYNC_TIME_OUT_SYNC  ((DWORD)0xC00A002AL)

//
// MessageId: AFRES_COMMLIB_MAX
//
// MessageText:
//
// %0
//
#define AFRES_COMMLIB_MAX                ((DWORD)0xC00A002BL)

 // The following are the message definitions for Common
//
// MessageId: AFRES_AFALOG_INFO
//
// MessageText:
//
// Information%0
//
#define AFRES_AFALOG_INFO                ((DWORD)0x400B0001L)

//
// MessageId: AFRES_AFALOG_WARNING
//
// MessageText:
//
// Warning%0
//
#define AFRES_AFALOG_WARNING             ((DWORD)0x400B0002L)

//
// MessageId: AFRES_AFALOG_ERROR
//
// MessageText:
//
// Error%0
//
#define AFRES_AFALOG_ERROR               ((DWORD)0x400B0003L)

//
// MessageId: AFRES_AFALOG_HOUR
//
// MessageText:
//
//  Hr%0
//
#define AFRES_AFALOG_HOUR                ((DWORD)0x400B0004L)

//
// MessageId: AFRES_AFALOG_MINUTE
//
// MessageText:
//
//  Min%0
//
#define AFRES_AFALOG_MINUTE              ((DWORD)0x400B0005L)

//
// MessageId: AFRES_AFALOG_SECOND
//
// MessageText:
//
//  Sec%0
//
#define AFRES_AFALOG_SECOND              ((DWORD)0x400B0006L)

//
// MessageId: AFRES_AFALOG_AUTOUPDATE_INFO
//
// MessageText:
//
// Updates Information%0
//
#define AFRES_AFALOG_AUTOUPDATE_INFO     ((DWORD)0x400B0007L)

//
// MessageId: AFRES_AFALOG_AUTOUPDATE_WARNING
//
// MessageText:
//
// Updates Warning%0
//
#define AFRES_AFALOG_AUTOUPDATE_WARNING  ((DWORD)0x400B0008L)

//
// MessageId: AFRES_AFALOG_AUTOUPDATE_ERROR
//
// MessageText:
//
// Updates Error%0
//
#define AFRES_AFALOG_AUTOUPDATE_ERROR    ((DWORD)0x400B0009L)

//
// MessageId: AFRES_AFALOG_PRODUCTNAME_HBVB
//
// MessageText:
//
// ^AU_ProductName_HBVB_SHORT^%0
//
#define AFRES_AFALOG_PRODUCTNAME_HBVB    ((DWORD)0x400B000AL)

//
// MessageId: AFRES_AFALOG_LAST_ERROR_DETAIL
//
// MessageText:
//
// Error details: (EC=[0x%1!08x!], MSG=[%2!s!])%0
//
#define AFRES_AFALOG_LAST_ERROR_DETAIL   ((DWORD)0x400B000BL)

//
// MessageId: IDS_UPGRADE_IMPORT_ACTIVITYLOG
//
// MessageText:
//
// There are %1!d! existing activity log messages from previous release detected, which are not imported to current activity log database. Please contact arcserve support to import them after installation.%0
//
#define IDS_UPGRADE_IMPORT_ACTIVITYLOG   ((DWORD)0x800B000CL)

//
// MessageId: AFRES_AFALOG_Mbps
//
// MessageText:
//
//  Mbps%0
//
#define AFRES_AFALOG_Mbps                ((DWORD)0x400B000DL)

//
// MessageId: AFRES_AFALOG_Kbps
//
// MessageText:
//
//  Kbps%0
//
#define AFRES_AFALOG_Kbps                ((DWORD)0x400B000EL)

//
// MessageId: AFRES_AFCOMM_SESSPWD_TOO_LONG
//
// MessageText:
//
// Encryption password length is too long. The maximum length is %1!u! characters.%0
//
#define AFRES_AFCOMM_SESSPWD_TOO_LONG    ((DWORD)0xC00B000FL)

//
// MessageId: AFRES_AFCOMM_NIC_DISCONNECTED
//
// MessageText:
//
// Network adapter changes detected: [%1!s!] media disconnected.%0
//
#define AFRES_AFCOMM_NIC_DISCONNECTED    ((DWORD)0x800B0010L)

//
// MessageId: AFRES_AFCOMM_NIC_CONNECTED
//
// MessageText:
//
// Network adapter changes detected: [%1!s!] media connected.%0
//
#define AFRES_AFCOMM_NIC_CONNECTED       ((DWORD)0x400B0011L)

//
// MessageId: AFRES_AFCOMM_SESS_LOCKED_BY_OTHERS
//
// MessageText:
//
// Session resources are being used by process %1!s! on computer %2!s!.%0
//
#define AFRES_AFCOMM_SESS_LOCKED_BY_OTHERS ((DWORD)0xC00B0012L)

//
// MessageId: AFRES_AFCOMM_BK_INFO_DATA_STORE
//
// MessageText:
//
// The backup destination is data store %1!s!(%2!s!) on RPS %3!s!.%0
//
#define AFRES_AFCOMM_BK_INFO_DATA_STORE  ((DWORD)0x400B0013L)

//
// MessageId: AFRES_AFCOMM_SESS_PWD_IS_INCORRECT
//
// MessageText:
//
// Session password is incorrect. (Session Number=[%1!u!])%0
//
#define AFRES_AFCOMM_SESS_PWD_IS_INCORRECT ((DWORD)0xC00B0014L)

//
// MessageId: AFRES_AFCOMM_DS_HASH_KEY_IS_INCORRECT
//
// MessageText:
//
// Data store hash key is incorrect. (Data Store Name=[%1!s!], Data Store GUID=[%2!s!])%0
//
#define AFRES_AFCOMM_DS_HASH_KEY_IS_INCORRECT ((DWORD)0xC00B0015L)

//
// MessageId: AFRES_AFCOMM_E1000E_DISABLED
//
// MessageText:
//
// Network adapter [%1!s!] has been detected on node [%2!s!]. As a result, the TCP segmentation offload (TSO) feature (also known as the Large Send Offload) of this adapter has been disabled to avoid any potential data corruption.%0
//
#define AFRES_AFCOMM_E1000E_DISABLED     ((DWORD)0x800B0016L)

//
// MessageId: AFRES_AFCOMM_E1000E_DISABLE_FAIL
//
// MessageText:
//
// Network adapter [%1!s!] has been detected on node [%2!s!]. This adapter may cause data corruption. To avoid this problem, change the network adapter or manually disable the TCP segmentation offload (TSO) feature of the adapter (also known as the Large Send Offload).%0
//
#define AFRES_AFCOMM_E1000E_DISABLE_FAIL ((DWORD)0x800B0017L)

//
// MessageId: AFRES_AFCOIF_DEST_IN_USE
//
// MessageText:
//
// The backup destination you specified is currently in use, please enter another backup destination.%0
//
#define AFRES_AFCOIF_DEST_IN_USE         ((DWORD)0xC00B0018L)

//
// MessageId: AFRES_MOUNTTO_FAT32
//
// MessageText:
//
// Failed to mount volume to the directory. The directory must be NTFS volume or ReFS volume.%0
//
#define AFRES_MOUNTTO_FAT32              ((DWORD)0xC00B0019L)

//
// MessageId: AFRES_MOUNTTO_NET_DRIVE_LETTER
//
// MessageText:
//
// The selected drive letter is already in use by another user. Select another drive letter.%0
//
#define AFRES_MOUNTTO_NET_DRIVE_LETTER   ((DWORD)0xC00B001AL)

//
// MessageId: AFRES_ACT_ERROR_MOUNT_DRIVER_NOT_WORK
//
// MessageText:
//
// Mount driver is not working properly, please try to reinstall mount driver.%0
//
#define AFRES_ACT_ERROR_MOUNT_DRIVER_NOT_WORK ((DWORD)0xC00B001BL)

//
// MessageId: AFRES_ACT_ERROR_MOUNT_VOLUME_NOT_READY
//
// MessageText:
//
// Mounting the volume takes longer time than expected (%!1!d minutes). It may happen when your server has unexpected load. Retry when the server load is lesser or refer to the troubleshooting in online documentation to increase the time out value.%0
//
#define AFRES_ACT_ERROR_MOUNT_VOLUME_NOT_READY ((DWORD)0xC00B001CL)

//
// MessageId: AFRES_ACT_ERROR_MOUNT_NO_FILESYSTEM
//
// MessageText:
//
// The mount volume does not contain a recognizable file system. Verify the availability or data integrity of the recovery point.%0
//
#define AFRES_ACT_ERROR_MOUNT_NO_FILESYSTEM ((DWORD)0xC00B001DL)

//
// MessageId: AFRES_ACT_SUCCEED_MOUNT_SESSION
//
// MessageText:
//
// Volume[%1!s!] from recovery point[%2!s!] has been mounted to [%3!s!] successfully.%0
//
#define AFRES_ACT_SUCCEED_MOUNT_SESSION  ((DWORD)0x400B001EL)

//
// MessageId: AFRES_ACT_FAIL_MOUNT_SESSION
//
// MessageText:
//
// Failed to mount volume[%1!s!] from recovery point[%2!s!] to [%3!s!] with error %4!d![%5!s!].%0
//
#define AFRES_ACT_FAIL_MOUNT_SESSION     ((DWORD)0xC00B001FL)

//
// MessageId: AFRES_ACT_SUCCEED_DISMOUNT_SESSION
//
// MessageText:
//
// Volume [%1!s!] has been dismounted successfully.%0
//
#define AFRES_ACT_SUCCEED_DISMOUNT_SESSION ((DWORD)0x400B0020L)

//
// MessageId: AFRES_ACT_FAIL_DISMOUNT_SESSION
//
// MessageText:
//
// Failed to dismount volume [%1!s!] with error %2!d![%3!s!].%0
//
#define AFRES_ACT_FAIL_DISMOUNT_SESSION  ((DWORD)0xC00B0021L)

//
// MessageId: AFRES_ACT_MOUNT_MERGE_SESSION
//
// MessageText:
//
// Failed to mount the specified recovery point because another (backup/catalog/merge) operation is in progress.%0
//
#define AFRES_ACT_MOUNT_MERGE_SESSION    ((DWORD)0xC00B0022L)

//
// MessageId: AFRES_ACT_FAIL_OPEN_MOUNTING_DRIVER
//
// MessageText:
//
// The driver used to mount the recovery point to the volume is not functioning. Verify that it is installed properly.%0
//
#define AFRES_ACT_FAIL_OPEN_MOUNTING_DRIVER ((DWORD)0xC00B0023L)

//
// MessageId: AFRES_ACT_MOUNT_INVALID_SESSION
//
// MessageText:
//
// The selected recovery point is invalid. Ensure that a valid full recovery point is accessible.%0
//
#define AFRES_ACT_MOUNT_INVALID_SESSION  ((DWORD)0xC00B0024L)

//
// MessageId: AFRES_ACT_SUCCEED_MOUNT_SESSION_2
//
// MessageText:
//
// Volume [%1!s!] from ^AU_ProductName_SERVER^ [%2!s!], data store [%3!s!], Session [%4!d!] has been successfully mounted to drive [%5!s!].%0
//
#define AFRES_ACT_SUCCEED_MOUNT_SESSION_2 ((DWORD)0x400B0025L)

//
// MessageId: AFRES_ACT_FAIL_MOUNT_SESSION_2
//
// MessageText:
//
// Failed to mount volume [%1!s!] from ^AU_ProductName_SERVER^ [%2!s!], data store [%3!s!], Session [%4!d!] to drive [%5!s!] with error %6!d![%7!s!].%0
//
#define AFRES_ACT_FAIL_MOUNT_SESSION_2   ((DWORD)0xC00B0026L)

//
// MessageId: AFRES_AFDRCO_VOLUME_LABEL_EFI
//
// MessageText:
//
// EFI System Partition%0
//
#define AFRES_AFDRCO_VOLUME_LABEL_EFI    ((DWORD)0x400B0027L)

//
// MessageId: AFRES_AFDRCO_VOLUME_LABEL_RECOVERY
//
// MessageText:
//
// Recovery Partition%0
//
#define AFRES_AFDRCO_VOLUME_LABEL_RECOVERY ((DWORD)0x400B0028L)

//
// MessageId: AFRES_AFCOFC_MOVE_DATA_SUCCESS
//
// MessageText:
//
// Successfully moved old backup data of [%1!s!].%0
//
#define AFRES_AFCOFC_MOVE_DATA_SUCCESS   ((DWORD)0x400B0029L)

//
// MessageId: AFRES_AFCOFC_MOVE_DATA_FAILED
//
// MessageText:
//
// Failed to move old backup data of [%1!s!].%0
//
#define AFRES_AFCOFC_MOVE_DATA_FAILED    ((DWORD)0xC00B002AL)

//
// MessageId: AFRES_AFCOFC_DEST_IN_CHAIN
//
// MessageText:
//
// Backup destination[%1!s!] is already in a backup chain. Unable to back up data to this destination.%0
//
#define AFRES_AFCOFC_DEST_IN_CHAIN       ((DWORD)0xC00B002BL)

//
// MessageId: AFRES_AFCOFC_DEST_CONTAIN_OLD_BACKUP
//
// MessageText:
//
// Backup destination[%1!s!] contains previously backed up data. Session numbers will be assigned based on the sessions already applied to the old backup data.%0
//
#define AFRES_AFCOFC_DEST_CONTAIN_OLD_BACKUP ((DWORD)0x800B002CL)

//
// MessageId: AFRES_AFCOFC_INVALID_ADMINACCOUNT
//
// MessageText:
//
// The account[%1!s!] for ^AU_ProductName_AGENT_SHORT^ is invalid.%0
//
#define AFRES_AFCOFC_INVALID_ADMINACCOUNT ((DWORD)0x800B002DL)

//
// MessageId: AFRES_AFCOFC_UNABLE_PWDMGMT_INIT
//
// MessageText:
//
// Unable to initialize key management module.%0
//
#define AFRES_AFCOFC_UNABLE_PWDMGMT_INIT ((DWORD)0xC00B002EL)

//
// MessageId: AFRES_AFCOFC_MASTERKEY_MISMATCH
//
// MessageText:
//
// Key management is inactive because of mismatched master key for this machine.%0
//
#define AFRES_AFCOFC_MASTERKEY_MISMATCH  ((DWORD)0x800B002FL)

//
// MessageId: AFRES_AFCOFC_UNABLE_FOUND_SESSPWD
//
// MessageText:
//
// Unable to find session password in key management database. (Session GUID=[%1!s!])%0
//
#define AFRES_AFCOFC_UNABLE_FOUND_SESSPWD ((DWORD)0xC00B0030L)

//
// MessageId: AFRES_AFCOFC_FOUND_SESSPWD_SUCCEED
//
// MessageText:
//
// Session password retrieved by key management. (Session GUID=[%1!s!])%0
//
#define AFRES_AFCOFC_FOUND_SESSPWD_SUCCEED ((DWORD)0x400B0031L)

//
// MessageId: AFRES_AFCOFC_UNABLE_REMOVE_SESSPWD
//
// MessageText:
//
// Unable to remove session password from key management database. (Session GUID=[%1!s!])%0
//
#define AFRES_AFCOFC_UNABLE_REMOVE_SESSPWD ((DWORD)0x800B0032L)

//
// MessageId: AFRES_AFCOFC_REMOVE_SESSPWD_SUCCEED
//
// MessageText:
//
// Session password successfully removed from key management. (Session GUID=[%1!s!])%0
//
#define AFRES_AFCOFC_REMOVE_SESSPWD_SUCCEED ((DWORD)0x400B0033L)

//
// MessageId: AFRES_AFCOFC_UNABLE_SHRINK_KEYMGMT
//
// MessageText:
//
// Unable to shrink key management database file and remove all passwords marked as "removed".%0
//
#define AFRES_AFCOFC_UNABLE_SHRINK_KEYMGMT ((DWORD)0xC00B0034L)

//
// MessageId: AFRES_AFCOFC_SHRINK_KEYMGMT_SUCCEED
//
// MessageText:
//
// Able to successfully shrink key management database file and removed all passwords marked as "removed".%0
//
#define AFRES_AFCOFC_SHRINK_KEYMGMT_SUCCEED ((DWORD)0x400B0035L)

//
// MessageId: AFRES_AFCOFC_UNABLE_UPDATE_KEYMGMT_ADMIN
//
// MessageText:
//
// Unable to update administration account for key management database. (UserName=[%1!s!])%0
//
#define AFRES_AFCOFC_UNABLE_UPDATE_KEYMGMT_ADMIN ((DWORD)0xC00B0036L)

//
// MessageId: AFRES_AFCOFC_UPDATE_KEYMGMT_ADMIN_SUCCEED
//
// MessageText:
//
// Successfully updated administration account for key management database. (UserName=[%1!s!])%0
//
#define AFRES_AFCOFC_UPDATE_KEYMGMT_ADMIN_SUCCEED ((DWORD)0x400B0037L)

//
// MessageId: AFRES_AFCOFC_UNABLE_ADD_SESSPWD
//
// MessageText:
//
// Unable to add session password to key management database. (Session GUID=[%1!s!])%0
//
#define AFRES_AFCOFC_UNABLE_ADD_SESSPWD  ((DWORD)0x800B0038L)

//
// MessageId: AFRES_AFCOFC_ADD_SESSPWD_SUCCEED
//
// MessageText:
//
// Successfully added session password to key management database. (Session GUID=[%1!s!]).%0
//
#define AFRES_AFCOFC_ADD_SESSPWD_SUCCEED ((DWORD)0x400B0039L)

//
// MessageId: AFRES_AFCOFC_CRP_CONTAIN_OLD_BACKUP
//
// MessageText:
//
// Copy of recovery point destination[%1!s!] contains previously backed up data. Session numbers will be assigned based on the sessions already applied to the old backup data.%0
//
#define AFRES_AFCOFC_CRP_CONTAIN_OLD_BACKUP ((DWORD)0x800B003AL)

//
// MessageId: AFRES_AFSTORHBA_ADMIN_INVALID
//
// MessageText:
//
// The credential information of %1!s! is invalid.
//
#define AFRES_AFSTORHBA_ADMIN_INVALID    ((DWORD)0xC00B003BL)

//
// MessageId: AFRES_AFSTORHBA_FOLDER_ACCESS_RIGHT
//
// MessageText:
//
// Please make sure that the folder (%1!s!) has users group access right.%0
//
#define AFRES_AFSTORHBA_FOLDER_ACCESS_RIGHT ((DWORD)0xC00B003CL)

//
// MessageId: AFRES_AFSTORHBA_LOCK_SESSION_FAILED
//
// MessageText:
//
// Failed to lock the session %1!d! at backup destination %2!s!.%0
//
#define AFRES_AFSTORHBA_LOCK_SESSION_FAILED ((DWORD)0xC00B003DL)

//
// MessageId: AFRES_LIC_SQL_OPTION
//
// MessageText:
//
// SQL Option%0
//
#define AFRES_LIC_SQL_OPTION             ((DWORD)0x400B003EL)

//
// MessageId: AFRES_LIC_ENCRYPTION
//
// MessageText:
//
// Encryption%0
//
#define AFRES_LIC_ENCRYPTION             ((DWORD)0x400B003FL)

//
// MessageId: AFRES_LIC_SCHEDULEDEXPORT
//
// MessageText:
//
// Simple Virtual Converter%0
//
#define AFRES_LIC_SCHEDULEDEXPORT        ((DWORD)0x400B0040L)

//
// MessageId: AFRES_LIC_EXCHANGE_DB_RECOVERY
//
// MessageText:
//
// Exchange DB Recovery%0
//
#define AFRES_LIC_EXCHANGE_DB_RECOVERY   ((DWORD)0x400B0041L)

//
// MessageId: AFRES_LIC_EXCHANGE_GR_RECOVERY
//
// MessageText:
//
// Exchange Granular Recovery%0
//
#define AFRES_LIC_EXCHANGE_GR_RECOVERY   ((DWORD)0x400B0042L)

//
// MessageId: AFRES_EDGE_UTL_USAGE_1
//
// MessageText:
//
// Usage: %1!s! %2!s!%0
//
#define AFRES_EDGE_UTL_USAGE_1           ((DWORD)0x400B0043L)

//
// MessageId: AFRES_EDGE_UTL_USAGE_2
//
// MessageText:
//
// %1!s! ^AU_ProductName_CONSOLE_SHORT^%0
//
#define AFRES_EDGE_UTL_USAGE_2           ((DWORD)0x400B0044L)

//
// MessageId: AFRES_EDGE_UTL_USAGE_3
//
// MessageText:
//
// %1!s! ^AU_ProductName_HBVB_SHORT^%0
//
#define AFRES_EDGE_UTL_USAGE_3           ((DWORD)0x400B0045L)

//
// MessageId: AFRES_EDGE_UTL_USAGE_4
//
// MessageText:
//
// %1!s! ^AU_ProductName_VS_SHORT^%0
//
#define AFRES_EDGE_UTL_USAGE_4           ((DWORD)0x400B0046L)

//
// MessageId: AFRES_EDGE_UTL_CLEAN_FAILED
//
// MessageText:
//
// The processing of breaking the management relationship between ^AU_ProductName_AGENT_SHORT^ and ^AU_ProductName_COMMON^ did not complete successfully.%0
//
#define AFRES_EDGE_UTL_CLEAN_FAILED      ((DWORD)0x400B0047L)

//
// MessageId: AFRES_EDGE_UTL_CLEAN_SUCCEED
//
// MessageText:
//
// The processing of breaking the management relationship between ^AU_ProductName_AGENT_SHORT^ and ^AU_ProductName_COMMON^ completed successfully.%0
//
#define AFRES_EDGE_UTL_CLEAN_SUCCEED     ((DWORD)0x400B0048L)

//
// MessageId: AFRES_EDGE_UTL_CLEAN_PM
//
// MessageText:
//
// The processing of breaking the management relationship between ^AU_ProductName_AGENT_SHORT^ and ^AU_ProductName_CONSOLE_SHORT^ is in progress.%0
//
#define AFRES_EDGE_UTL_CLEAN_PM          ((DWORD)0x400B0049L)

//
// MessageId: AFRES_EDGE_UTL_CLEAN_HBVB
//
// MessageText:
//
// The processing of breaking the management relationship between ^AU_ProductName_AGENT_SHORT^ and ^AU_ProductName_HBVB_SHORT^ is in progress.%0
//
#define AFRES_EDGE_UTL_CLEAN_HBVB        ((DWORD)0x400B004AL)

//
// MessageId: AFRES_EDGE_UTL_CLEAN_VS
//
// MessageText:
//
// The processing of breaking the management relationship between ^AU_ProductName_AGENT_SHORT^ and ^AU_ProductName_VS_SHORT^ is in progress.%0
//
#define AFRES_EDGE_UTL_CLEAN_VS          ((DWORD)0x400B004BL)

//
// MessageId: AFRES_EDGE_UTL_UNKNOWN_APP
//
// MessageText:
//
// The ^AU_ProductName_COMMON^ type is unknown.%0
//
#define AFRES_EDGE_UTL_UNKNOWN_APP       ((DWORD)0xC00B004CL)

//
// MessageId: AFRES_CI_D2DPLUGIN
//
// MessageText:
//
// %0
//
#define AFRES_CI_D2DPLUGIN               ((DWORD)0x400B004DL)

//
// MessageId: AFRES_DEDUPE_NO_DEDUPE
//
// MessageText:
//
// You are attempting to restore data that was backed up from an NTFS deduplication volume to a system that does not have the Windows 8 Data Deduplication server role installed and enabled(%1!s!  drive). As a result, you will not be able to access the restored deduplication files until this role is enabled at the restore destination. After the Data Deduplication role is enabled, these restored files will be automatically displayed and no additional restore job is necessary.%0
//
#define AFRES_DEDUPE_NO_DEDUPE           ((DWORD)0xC00B004EL)

//
// MessageId: AFRES_COMMON_ERR_CREATEFILE
//
// MessageText:
//
// Failed to create session lock file %1!s!. Error code:%2!d! (%3!s!).%0
//
#define AFRES_COMMON_ERR_CREATEFILE      ((DWORD)0xC00B004FL)

//
// MessageId: AFRES_COMMON_ERR_READFILE
//
// MessageText:
//
// Failed to read data from session lock file %1!s!. Error code:%2!d! (%3!s!).%0
//
#define AFRES_COMMON_ERR_READFILE        ((DWORD)0xC00B0050L)

//
// MessageId: AFRES_COMMON_ERR_WRITEFILE
//
// MessageText:
//
// Failed to write data to session lock file %1!s!. Error code:%2!d! (%3!s!).%0
//
#define AFRES_COMMON_ERR_WRITEFILE       ((DWORD)0xC00B0051L)

//
// MessageId: AFRES_COMMON_ERR_MUTEX
//
// MessageText:
//
// Failed to create session lock on %1!s!. Error code:%2!d!(%3!s!).%0
//
#define AFRES_COMMON_ERR_MUTEX           ((DWORD)0xC00B0052L)

//
// MessageId: AFRES_COMMON_INF_JOB_BACKUP
//
// MessageText:
//
// Backup%0
//
#define AFRES_COMMON_INF_JOB_BACKUP      ((DWORD)0x400B0053L)

//
// MessageId: AFRES_COMMON_INF_JOB_MERGE
//
// MessageText:
//
// Merge%0
//
#define AFRES_COMMON_INF_JOB_MERGE       ((DWORD)0x400B0054L)

//
// MessageId: AFRES_COMMON_INF_JOB_CATALOG
//
// MessageText:
//
// Catalog%0
//
#define AFRES_COMMON_INF_JOB_CATALOG     ((DWORD)0x400B0055L)

//
// MessageId: AFRES_COMMON_INF_JOB_RESTORE
//
// MessageText:
//
// Restore%0
//
#define AFRES_COMMON_INF_JOB_RESTORE     ((DWORD)0x400B0056L)

//
// MessageId: AFRES_COMMON_INF_JOB_EXPORT
//
// MessageText:
//
// Copy Recovery Point%0
//
#define AFRES_COMMON_INF_JOB_EXPORT      ((DWORD)0x400B0057L)

//
// MessageId: AFRES_COMMON_INF_JOB_HBBU
//
// MessageText:
//
// Host Base VM Backup%0
//
#define AFRES_COMMON_INF_JOB_HBBU        ((DWORD)0x400B0058L)

//
// MessageId: AFRES_COMMON_INF_JOB_ARCHIVE
//
// MessageText:
//
// File Copy%0
//
#define AFRES_COMMON_INF_JOB_ARCHIVE     ((DWORD)0x400B0059L)

//
// MessageId: AFRES_COMMON_INF_JOB_MOUNT
//
// MessageText:
//
// Mount Recovery Point%0
//
#define AFRES_COMMON_INF_JOB_MOUNT       ((DWORD)0x400B005AL)

//
// MessageId: AFRES_COMMON_INF_JOB_BMR
//
// MessageText:
//
// Bare Metal Recovery%0
//
#define AFRES_COMMON_INF_JOB_BMR         ((DWORD)0x400B005BL)

//
// MessageId: AFRES_COMMON_INF_JOB_LITE
//
// MessageText:
//
// Lite Integration%0
//
#define AFRES_COMMON_INF_JOB_LITE        ((DWORD)0x400B005CL)

//
// MessageId: AFRES_COMMON_INF_JOB_VCM
//
// MessageText:
//
// Virtual Standby%0
//
#define AFRES_COMMON_INF_JOB_VCM         ((DWORD)0x400B005DL)

//
// MessageId: AFRES_COMMON_INF_JOB_REPLICATION
//
// MessageText:
//
// Replication%0
//
#define AFRES_COMMON_INF_JOB_REPLICATION ((DWORD)0x400B005EL)

//
// MessageId: AFRES_COMMON_ERR_LOCK_FAILED
//
// MessageText:
//
// Failed to lock session on %1!s! (detail information:%2!s!). Session was already locked by %3!s! job, Computer name:%4!s!, Process Id:%5!d!.%0
//
#define AFRES_COMMON_ERR_LOCK_FAILED     ((DWORD)0x800B005FL)

//
// MessageId: AFRES_COMMON_WAR_REPAIR_ACTLOG_DB
//
// MessageText:
//
// The SQLite database (which is used for saving the activity log) is damaged and cannot be accessed. Please contact arcserve support to repair the database.%0
//
#define AFRES_COMMON_WAR_REPAIR_ACTLOG_DB ((DWORD)0xC00B0060L)

//
// MessageId: AFRES_COMMON_WAR_REPAIR_HISTORY_DB
//
// MessageText:
//
// The SQLite database (which is used for saving the job history) is damaged and cannot be accessed. Please contact arcserve support to repair the database.%0
//
#define AFRES_COMMON_WAR_REPAIR_HISTORY_DB ((DWORD)0xC00B0061L)

//
// MessageId: AFRES_COMMON_INF_JOB_IVM
//
// MessageText:
//
// ^AU_ProductName_IVM_SHORT^%0
//
#define AFRES_COMMON_INF_JOB_IVM         ((DWORD)0x400B0062L)

//
// MessageId: AFRES_COMMON_INF_JOB_START_IVM
//
// MessageText:
//
// Start ^AU_ProductName_IVM_SHORT^%0
//
#define AFRES_COMMON_INF_JOB_START_IVM   ((DWORD)0x400B0063L)

//
// MessageId: AFRES_COMMON_INF_JOB_STOP_IVM
//
// MessageText:
//
// Stop ^AU_ProductName_IVM_SHORT^%0
//
#define AFRES_COMMON_INF_JOB_STOP_IVM    ((DWORD)0x400B0064L)

//
// MessageId: AFRES_COMMON_INF_JOB_ASSURED_BACKUP
//
// MessageText:
//
// ^AU_ProductName_AR_SHORT^%0
//
#define AFRES_COMMON_INF_JOB_ASSURED_BACKUP ((DWORD)0x400B0065L)

//
// MessageId: AFRES_COMMON_INF_JOB_IVHD
//
// MessageText:
//
// ^AU_ProductName_IVHD_SHORT^%0
//
#define AFRES_COMMON_INF_JOB_IVHD        ((DWORD)0x400B0066L)

//
// MessageId: AFRES_COMMON_INF_JOB_START_IVHD
//
// MessageText:
//
// Start ^AU_ProductName_IVHD_SHORT^%0
//
#define AFRES_COMMON_INF_JOB_START_IVHD  ((DWORD)0x400B0067L)

//
// MessageId: AFRES_COMMON_INF_JOB_STOP_IVHD
//
// MessageText:
//
// Stop ^AU_ProductName_IVHD_SHORT^%0
//
#define AFRES_COMMON_INF_JOB_STOP_IVHD   ((DWORD)0x400B0068L)

//
// MessageId: AFRES_COMMON_HELPER_SERVICE_DISPALY_NAME
//
// MessageText:
//
// ^AU_ProductName_UDP_SHORT^ VM Helper Service%0
//
#define AFRES_COMMON_HELPER_SERVICE_DISPALY_NAME ((DWORD)0x400B0069L)

//
// MessageId: AFRES_COMMON_HELPER_SERVICE_DESCRIPTION
//
// MessageText:
//
// Install virtual machine integration tools and setup network resources on virtual machine startup.%0
//
#define AFRES_COMMON_HELPER_SERVICE_DESCRIPTION ((DWORD)0x400B006AL)

//
// MessageId: AFRES_AFBKDL_ENCINFO_ENABLED
//
// MessageText:
//
// Encryption is enabled%0
//
#define AFRES_AFBKDL_ENCINFO_ENABLED     ((DWORD)0x400B006BL)

//
// MessageId: AFRES_COMMON_FILE_ACCESS_DENIED
//
// MessageText:
//
// The account %1!s! does not have write permission of file %2!s!. You need to add complete access to this account on this file and try again.%0
//
#define AFRES_COMMON_FILE_ACCESS_DENIED  ((DWORD)0xC00B006CL)

 // The following are the message definitions for CopySession
//
// MessageId: AFRES_AFBKDL_COPY_SESSION_START
//
// MessageText:
//
// [PID: %1!d!] Starting copy of recovery point created on %2!s!.%0
//
#define AFRES_AFBKDL_COPY_SESSION_START  ((DWORD)0x400C0001L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_BASE
//
// MessageText:
//
// [PID: %1!d!] Starting copy of recovery point created on %2!s!.%0
//
#define AFRES_AFBKDL_COPY_SESSION_BASE   ((DWORD)0x400C0002L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SESS_PATH
//
// MessageText:
//
// Copy recovery point from %1!s!, session [%2!d!] to %3!s!.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SESS_PATH ((DWORD)0x400C0003L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_FINISHED
//
// MessageText:
//
// Copy recovery point finished.%0
//
#define AFRES_AFBKDL_COPY_SESSION_FINISHED ((DWORD)0x400C0004L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_FAILED
//
// MessageText:
//
// Copy recovery point failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_FAILED ((DWORD)0xC00C0005L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_FAILED_VDISK
//
// MessageText:
//
// Copy recovery point for volume %1!s! failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_FAILED_VDISK ((DWORD)0xC00C0006L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SUCCESS_VDISK
//
// MessageText:
//
// Copy recovery point for volume %1!s! succeeded.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SUCCESS_VDISK ((DWORD)0x400C0007L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SUCCESS_SESSMETADATA
//
// MessageText:
//
// Copy session meta data success.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SUCCESS_SESSMETADATA ((DWORD)0x400C0008L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_FAILED_COPYCATALOG
//
// MessageText:
//
// Copy catalog files failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_FAILED_COPYCATALOG ((DWORD)0xC00C0009L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_FAILED_COPYBLOCK
//
// MessageText:
//
// Copy session block files failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_FAILED_COPYBLOCK ((DWORD)0xC00C000AL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_CANCELLED
//
// MessageText:
//
// Copy recovery point canceled.%0
//
#define AFRES_AFBKDL_COPY_SESSION_CANCELLED ((DWORD)0x400C000BL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_COPYERR
//
// MessageText:
//
// Copy file %1!s! to %2!s! failed, Reason:%3!s!.%0
//
#define AFRES_AFBKDL_COPY_SESSION_COPYERR ((DWORD)0xC00C000CL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SPACE_ERR
//
// MessageText:
//
// %1!s! does not have enough free space. Please make sure you allocate %2!s! free space.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SPACE_ERR ((DWORD)0xC00C000DL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SPACE_WAR
//
// MessageText:
//
// %1!s! may be not have enough free space. Please make sure you allocate at least %2!s! free space.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SPACE_WAR ((DWORD)0x800C000EL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_OPENBLOCK_ERR
//
// MessageText:
//
// Open metablock failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_OPENBLOCK_ERR ((DWORD)0xC00C000FL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_READBLOCK_ERR
//
// MessageText:
//
// Read metablock failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_READBLOCK_ERR ((DWORD)0xC00C0010L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_OPENVHD_ERR
//
// MessageText:
//
// Open virtual disk %1!s! failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_OPENVHD_ERR ((DWORD)0xC00C0011L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_READVHD_ERR
//
// MessageText:
//
// Read data failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_READVHD_ERR ((DWORD)0xC00C0012L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SEEKVHD_ERR
//
// MessageText:
//
// Seek file pointer failed.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SEEKVHD_ERR ((DWORD)0xC00C0013L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_WRITEVHD_ERR
//
// MessageText:
//
// Write data failed. %1!s!%0
//
#define AFRES_AFBKDL_COPY_SESSION_WRITEVHD_ERR ((DWORD)0xC00C0014L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_FAILED_ERR
//
// MessageText:
//
// Failed to copy recovery point. Error:%1!s!.%0
//
#define AFRES_AFBKDL_COPY_SESSION_FAILED_ERR ((DWORD)0xC00C0015L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_WRITEDISK_WAR
//
// MessageText:
//
// Failed to write data to the destination folder, will retry now.%0
//
#define AFRES_AFBKDL_COPY_SESSION_WRITEDISK_WAR ((DWORD)0x800C0016L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_COMPRESS
//
// MessageText:
//
// %1!s! for copied recovery point.%0
//
#define AFRES_AFBKDL_COPY_SESSION_COMPRESS ((DWORD)0x400C0017L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SUMMARY
//
// MessageText:
//
// Total copied data %1!s!, Elapsed time %2!s!, Average read throughput %3!s!/Min.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SUMMARY ((DWORD)0x400C0018L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SUMMARY_SAVESPACE
//
// MessageText:
//
// Total copied data %1!s!, Elapsed time %2!s!, Average throughput %3!s!/Min, Space saved using compression:%4!s!%%.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SUMMARY_SAVESPACE ((DWORD)0x400C0019L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_ENC_NONE
//
// MessageText:
//
// The recovery point being copied is not encrypted.%0
//
#define AFRES_AFBKDL_COPY_SESSION_ENC_NONE ((DWORD)0x400C001AL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_ENC_AES128
//
// MessageText:
//
// The encryption type for the copied recovery point is AES-128.%0
//
#define AFRES_AFBKDL_COPY_SESSION_ENC_AES128 ((DWORD)0x400C001BL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_ENC_AES192
//
// MessageText:
//
// The encryption type for the copied recovery point is AES-192.%0
//
#define AFRES_AFBKDL_COPY_SESSION_ENC_AES192 ((DWORD)0x400C001CL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_ENC_AES256
//
// MessageText:
//
// The encryption type for the copied recovery point is AES-256.%0
//
#define AFRES_AFBKDL_COPY_SESSION_ENC_AES256 ((DWORD)0x400C001DL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SKIP_DISK
//
// MessageText:
//
// Virtual disk %1!s! does not contain volume information. The next copy session will skip the virtual disk.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SKIP_DISK ((DWORD)0x800C001EL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_LOCK_FAILED
//
// MessageText:
//
// The session on %1!s! was locked by a user from %2!s!.%0
//
#define AFRES_AFBKDL_COPY_SESSION_LOCK_FAILED ((DWORD)0x800C001FL)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_SKIP_SPECDISK
//
// MessageText:
//
// Skip copying disks (%1!s!) from the copy recovery point job. These disks were configured using storage spaces.%0
//
#define AFRES_AFBKDL_COPY_SESSION_SKIP_SPECDISK ((DWORD)0x800C0020L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_FAIL_LOCATE_DEST
//
// MessageText:
//
// Failed to locate copy of recovery point destination. (Path=[%1!s!], Username=[%2!s!], EC=[%3!d!])%0
//
#define AFRES_AFBKDL_COPY_SESSION_FAIL_LOCATE_DEST ((DWORD)0xC00C0021L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_WITH_DATASTORE
//
// MessageText:
//
// Copy recovery point from ^AU_ProductName_SERVER^ [%1!s!], data store [%2!s!], node [%3!s!], session [%4!d!]to %5!s!.%0
//
#define AFRES_AFBKDL_COPY_SESSION_WITH_DATASTORE ((DWORD)0x400C0022L)

//
// MessageId: AFRES_AFBKDL_COPY_SESSION_NO_BMRINFO
//
// MessageText:
//
// No Bare Metal Recovery information found. The copied recovery point cannot be used to perform Bare Metal Recovery.%0
//
#define AFRES_AFBKDL_COPY_SESSION_NO_BMRINFO ((DWORD)0x800C0023L)

//
// MessageId: AFRES_AFBKDL_PURGE_SESSION_FAILED
//
// MessageText:
//
// Failed to purge session %1!d!. Please verify if the copy recovery point destination is accessible or the session is opened by other jobs.%0
//
#define AFRES_AFBKDL_PURGE_SESSION_FAILED ((DWORD)0x800C0024L)

 // The following are the message definitions for DatastoreMgr
//
// MessageId: IDS_DATASTORE_MSG_SERVICE_START
//
// MessageText:
//
// The Data Store Management Service is started on ^AU_ProductName_SERVER_SHORT^ "%1!s!".%0
//
#define IDS_DATASTORE_MSG_SERVICE_START  ((DWORD)0x400D0001L)

//
// MessageId: IDS_DATASTORE_MSG_SERVICE_START_FAILED
//
// MessageText:
//
// The Data Store Management Service fail to start on ^AU_ProductName_SERVER_SHORT^ "%1!s!".%0
//
#define IDS_DATASTORE_MSG_SERVICE_START_FAILED ((DWORD)0xC00D0002L)

//
// MessageId: IDS_DATASTORE_MSG_SERVICE_STOP
//
// MessageText:
//
// The Data Store Management Service is stopped on ^AU_ProductName_SERVER_SHORT^ "%1!s!".%0
//
#define IDS_DATASTORE_MSG_SERVICE_STOP   ((DWORD)0x400D0003L)

//
// MessageId: IDS_DATASTORE_MSG_SERVICE_STOP_FAILED
//
// MessageText:
//
// The Data Store Management Service fail to Stop on ^AU_ProductName_SERVER_SHORT^ "%1!s!".%0
//
#define IDS_DATASTORE_MSG_SERVICE_STOP_FAILED ((DWORD)0xC00D0004L)

//
// MessageId: IDS_DATASTORE_FAIL_START_DS_FOR_LESS_MEMORY
//
// MessageText:
//
// Failed to start the deduplication data store "%1!s!" because the physical memory is not sufficient on the current server. You must have at least %2!d! MB of free physical memory space to start the data store.%0
//
#define IDS_DATASTORE_FAIL_START_DS_FOR_LESS_MEMORY ((DWORD)0xC00D0005L)

//
// MessageId: IDS_DATASTORE_REACH_MAX_DEDUPEDS_AMOUNT
//
// MessageText:
//
// Failed to create the new deduplication data store "%1!s!". The maximum number (%2!d!) of deduplication data stores has been reached.%0
//
#define IDS_DATASTORE_REACH_MAX_DEDUPEDS_AMOUNT ((DWORD)0xC00D0006L)

//
// MessageId: IDS_DATASTORE_FAIL_START_DS_FOR_OWNERSHIP
//
// MessageText:
//
// Failed to start data store "%1!s!" because it is owned by another server "%2!s!".%0
//
#define IDS_DATASTORE_FAIL_START_DS_FOR_OWNERSHIP ((DWORD)0xC00D0007L)

//
// MessageId: IDS_DATASTORE_FAIL_MODIFY_DS_FOR_OWNERSHIP
//
// MessageText:
//
// Failed to modify data store "%1!s!" because it is owned by another server "%2!s!".%0
//
#define IDS_DATASTORE_FAIL_MODIFY_DS_FOR_OWNERSHIP ((DWORD)0xC00D0008L)

//
// MessageId: IDS_DATASTORE_FAIL_START_DS_FOR_GUID_NOTME
//
// MessageText:
//
// Failed to start data store "%1!s!" because the destination path has been taken over by other data store. %0
//
#define IDS_DATASTORE_FAIL_START_DS_FOR_GUID_NOTME ((DWORD)0xC00D0009L)

//
// MessageId: IDS_DATASTORE_START_DS_VERIFY_FAIL_NODSNAME
//
// MessageText:
//
// Failed to start the data store because of an invalid data store path. Verify the verification files exist and have not been changed in the backup destination folder or any of the deduplication folders.%0
//
#define IDS_DATASTORE_START_DS_VERIFY_FAIL_NODSNAME ((DWORD)0xC00D000AL)

//
// MessageId: IDS_DATASTORE_START_DS_VERIFY_FAIL_DSNAME
//
// MessageText:
//
// Failed to start data store "%1!s!" because of an invalid data store path. Verify the verification files exist and have not been changed in the backup destination folder or any of the deduplication folders.%0
//
#define IDS_DATASTORE_START_DS_VERIFY_FAIL_DSNAME ((DWORD)0xC00D000BL)

//
// MessageId: IDS_DATASTORE_FAIL_LOCK_FOR_CONNECT
//
// MessageText:
//
// Failed to lock the data store "%1!s!", verify the network connection to the data store.%0
//
#define IDS_DATASTORE_FAIL_LOCK_FOR_CONNECT ((DWORD)0xC00D000CL)

//
// MessageId: IDS_DATASTORE_FAIL_LOCK_FOR_OWNERSHIP
//
// MessageText:
//
// Failed to lock data store "%1!s!" because it is already owned by another server "%2!s!". The data store is stopped automatically.%0
//
#define IDS_DATASTORE_FAIL_LOCK_FOR_OWNERSHIP ((DWORD)0xC00D000DL)

//
// MessageId: IDS_DATASTORE_FAIL_START_FOR_WRONG_USERPSW_NONAME
//
// MessageText:
//
// Failed to start the data store because an invalid Windows user name or an invalid password was provided. Update node and retry manually.%0
//
#define IDS_DATASTORE_FAIL_START_FOR_WRONG_USERPSW_NONAME ((DWORD)0xC00D000EL)

//
// MessageId: IDS_DATASTORE_FAIL_START_FOR_WRONG_USERPSW_NAME
//
// MessageText:
//
// Failed to start data store "%1!s!" because an invalid Windows user name or an invalid password was provided. Update node and retry manually.%0
//
#define IDS_DATASTORE_FAIL_START_FOR_WRONG_USERPSW_NAME ((DWORD)0xC00D000FL)

//
// MessageId: IDS_DATASTORE_IMPORT_DS_PATH_INACCESS
//
// MessageText:
//
// Failed to import the data store because the path is inaccessible. %0
//
#define IDS_DATASTORE_IMPORT_DS_PATH_INACCESS ((DWORD)0xC00D0010L)

//
// MessageId: IDS_DATASTORE_IMPORT_DS_GET_CONF_FAIL
//
// MessageText:
//
// Failed to import the data store because no valid configuration information was retrieved from the backup destination folder.%0
//
#define IDS_DATASTORE_IMPORT_DS_GET_CONF_FAIL ((DWORD)0xC00D0011L)

//
// MessageId: IDS_DATASTORE_IMPORT_INVALID_DS_PASSWORD
//
// MessageText:
//
// Failed to import the data store because an invalid data store password was provided.%0
//
#define IDS_DATASTORE_IMPORT_INVALID_DS_PASSWORD ((DWORD)0xC00D0012L)

//
// MessageId: IDS_DATASTORE_IMPORT_DS_HAS_LOCK
//
// MessageText:
//
// Failed to import the data store because it is locked by server: "%1!s!".%0
//
#define IDS_DATASTORE_IMPORT_DS_HAS_LOCK ((DWORD)0xC00D0013L)

//
// MessageId: IDS_DATASTORE_IMPORT_GDDDS_IN_USE
//
// MessageText:
//
// Failed to import the data store because the specified path is already being used by another data store.%0
//
#define IDS_DATASTORE_IMPORT_GDDDS_IN_USE ((DWORD)0xC00D0014L)

//
// MessageId: IDS_DATASTORE_IMPORT_MEM_TOO_LARGE
//
// MessageText:
//
// Failed to import the data store for the assigned physical memory is larger than actual one. %0
//
#define IDS_DATASTORE_IMPORT_MEM_TOO_LARGE ((DWORD)0xC00D0015L)

//
// MessageId: IDS_DATASTORE_IMPORT_MEM_TOO_SMALL
//
// MessageText:
//
// Failed to import the data store because the assigned physical memory space is insufficient.%0
//
#define IDS_DATASTORE_IMPORT_MEM_TOO_SMALL ((DWORD)0xC00D0016L)

//
// MessageId: IDS_DATASTORE_IMPORT_LOCK_DS_FAIL
//
// MessageText:
//
// Failed to import the data store because the data store could not be locked. Check that the lock file "DSExc.lck" (located in the destination path) is accessible.%0
//
#define IDS_DATASTORE_IMPORT_LOCK_DS_FAIL ((DWORD)0xC00D0017L)

//
// MessageId: IDS_DATASTORE_IMPORT_VERIFY_FAIL
//
// MessageText:
//
// Failed to import the data store because invalid data store paths were provided. Check the data store paths and try again.%0
//
#define IDS_DATASTORE_IMPORT_VERIFY_FAIL ((DWORD)0xC00D0018L)

//
// MessageId: IDS_DATASTORE_IMPORT_VERIFY_TIMESTAMP_FAIL
//
// MessageText:
//
// Failed to import the data store because the data consistency between the destination folder and the paths for the hash/data/index role do not match. Verify the paths point to the corresponding folders. %0
//
#define IDS_DATASTORE_IMPORT_VERIFY_TIMESTAMP_FAIL ((DWORD)0xC00D0019L)

//
// MessageId: IDS_DATASTORE_IMPORT_WRONG_PATH_INDEX
//
// MessageText:
//
// Failed to import the data store because invalid index role path was provided. Check the data store paths and try again.%0
//
#define IDS_DATASTORE_IMPORT_WRONG_PATH_INDEX ((DWORD)0xC00D001AL)

//
// MessageId: IDS_DATASTORE_IMPORT_WRONG_PATH_DATA
//
// MessageText:
//
// Failed to import the data store because invalid data role path was provided. Check the data store paths and try again.%0
//
#define IDS_DATASTORE_IMPORT_WRONG_PATH_DATA ((DWORD)0xC00D001BL)

//
// MessageId: IDS_DATASTORE_IMPORT_WRONG_PATH_HASH
//
// MessageText:
//
// Failed to import the data store because invalid hash role path was provided. Check the data store paths and try again.%0
//
#define IDS_DATASTORE_IMPORT_WRONG_PATH_HASH ((DWORD)0xC00D001CL)

//
// MessageId: IDS_DATASTORE_COPY_HASH_NO_ENOUGH_SPACE
//
// MessageText:
//
// Not enough free space exists in the destination hash path: %1!s!. %0
//
#define IDS_DATASTORE_COPY_HASH_NO_ENOUGH_SPACE ((DWORD)0xC00D001DL)

//
// MessageId: IDS_DATASTORE_COPY_HASH_FAILED
//
// MessageText:
//
// Failed to copy the hash files from the source hash path %1!s! to the destination hash path %2!s!. System error=[%3!s!]. %0
//
#define IDS_DATASTORE_COPY_HASH_FAILED   ((DWORD)0xC00D001EL)

 // The following are the message definitions for EventLog
//
// MessageId: UDP_EVENTLOG_ID_INFORMATION
//
// MessageText:
//
// %1%0
//
#define UDP_EVENTLOG_ID_INFORMATION      ((DWORD)0x40050064L)

//
// MessageId: UDP_EVENTLOG_ID_WARNING
//
// MessageText:
//
// %1%0
//
#define UDP_EVENTLOG_ID_WARNING          ((DWORD)0x40050065L)

//
// MessageId: UDP_EVENTLOG_ID_ERROR
//
// MessageText:
//
// %1%0
//
#define UDP_EVENTLOG_ID_ERROR            ((DWORD)0x40050066L)

//
// MessageId: UDP_EVENTLOG_JOB_ID
//
// MessageText:
//
// Job %1 - %0
//
#define UDP_EVENTLOG_JOB_ID              ((DWORD)0x40050067L)

 // The following are the message definitions for ExGrt
//
// MessageId: AFRES_EXGRT_FAILED_MOUNT_VHD
//
// MessageText:
//
// Failed to mount virtual disk of session %1!I64u! in folder %2!s!.%0
//
#define AFRES_EXGRT_FAILED_MOUNT_VHD     ((DWORD)0xC00E0001L)

//
// MessageId: AFRES_EXGRT_FAILED_RECOVERY_EDB
//
// MessageText:
//
// Failed to recover EDB file(%1!s!).%0
//
#define AFRES_EXGRT_FAILED_RECOVERY_EDB  ((DWORD)0xC00E0002L)

//
// MessageId: AFRES_EXGRT_MAILBOX_NOT_FOUND
//
// MessageText:
//
// Restore Failed: Mailbox %1!s! not found.%0
//
#define AFRES_EXGRT_MAILBOX_NOT_FOUND    ((DWORD)0xC00E0003L)

//
// MessageId: AFRES_EXGRT_FAILED_GEN_CATALOG
//
// MessageText:
//
// Failed to generate Exchange Granular Restore catalog for EDB File %1!s!. session=%2!d!, subsession=%3!d!%0
//
#define AFRES_EXGRT_FAILED_GEN_CATALOG   ((DWORD)0xC00E0004L)

//
// MessageId: AFRES_EXGRT_MAILBOX_COUNT
//
// MessageText:
//
// There are %1!d! mailbox(es) in the current database file(%2!s!).%0
//
#define AFRES_EXGRT_MAILBOX_COUNT        ((DWORD)0x400E0005L)

//
// MessageId: AFRES_EXGRT_MAPI_NOT_INSTALLED
//
// MessageText:
//
// Exchange Granular Restore cannot be done because Microsoft Exchange Server MAPI Client is not installed.  Please download MAPI from Microsoft, install it and retry.%0
//
#define AFRES_EXGRT_MAPI_NOT_INSTALLED   ((DWORD)0xC00E0006L)

//
// MessageId: AFRES_EXGRT_BEGIN_GEN_CATALOG
//
// MessageText:
//
// Begin generating Exchange Granular Restore catalog. session=%1!d!, subsession=%2!d!%0
//
#define AFRES_EXGRT_BEGIN_GEN_CATALOG    ((DWORD)0x400E0007L)

//
// MessageId: AFRES_EXGRT_END_GEN_CATALOG
//
// MessageText:
//
// Finished generating Exchange Granular Restore catalog. session=%1!d!, subsession=%2!d!%0
//
#define AFRES_EXGRT_END_GEN_CATALOG      ((DWORD)0x400E0008L)

//
// MessageId: AFRES_EXGRT_DES_DISK
//
// MessageText:
//
// Restore Item(s) to Disk(%1!s!).%0
//
#define AFRES_EXGRT_DES_DISK             ((DWORD)0x400E0009L)

//
// MessageId: AFRES_EXGRT_DES_ORIGINAL
//
// MessageText:
//
// Restore Item(s) to Original Mailbox(%1!s!).%0
//
#define AFRES_EXGRT_DES_ORIGINAL         ((DWORD)0x400E000AL)

//
// MessageId: AFRES_EXGRT_DES_ALTERNATIVE
//
// MessageText:
//
// Restore Item(s) to Alternative Mailbox(%1!s!).%0
//
#define AFRES_EXGRT_DES_ALTERNATIVE      ((DWORD)0x400E000BL)

//
// MessageId: AFRES_EXGRT_STORE_NOT_START
//
// MessageText:
//
// Microsoft Exchange Information Store Service is not in running state.%0
//
#define AFRES_EXGRT_STORE_NOT_START      ((DWORD)0x800E000CL)

//
// MessageId: AFRES_EXGRT_CHECK_SERVICE
//
// MessageText:
//
// The service is invalid: please check whether the Microsoft Exchange Information Store Service is in running state and whether the database which the current user(%1!s!) belongs to is in mounted state.%0
//
#define AFRES_EXGRT_CHECK_SERVICE        ((DWORD)0xC00E000DL)

//
// MessageId: AFRES_EXGRT_RESTORE_DISK_ERROR
//
// MessageText:
//
// Restore message %1!s! to disk error:%2!s!%0
//
#define AFRES_EXGRT_RESTORE_DISK_ERROR   ((DWORD)0xC00E000EL)

//
// MessageId: AFRES_EXGRT_RESTORE_DISK_NAME_ERROR
//
// MessageText:
//
// Restore message %1!s! fail, it's name too long.%0
//
#define AFRES_EXGRT_RESTORE_DISK_NAME_ERROR ((DWORD)0xC00E000FL)

//
// MessageId: AFRES_EXGRT_MAPI_INSTALL_ERROR
//
// MessageText:
//
// The MAPI package is installed improperly. please uninstall the MAPI package, and then reinstall it again%0
//
#define AFRES_EXGRT_MAPI_INSTALL_ERROR   ((DWORD)0xC00E0010L)

//
// MessageId: AFRES_EXGRT_LICENSE_INVALID_GRT
//
// MessageText:
//
// Please check the License of Exchange Granular Recovery, make sure it is valid and not expired.%0
//
#define AFRES_EXGRT_LICENSE_INVALID_GRT  ((DWORD)0xC00E0011L)

//
// MessageId: AFRES_EXGRT_LICENSE_INVALID_DB
//
// MessageText:
//
// Please check the License of Exchange DB Recovery, make sure it is valid and not expired.%0
//
#define AFRES_EXGRT_LICENSE_INVALID_DB   ((DWORD)0xC00E0012L)

//
// MessageId: AFRES_EXGRT_MAPI_DISPLAY_VERSION
//
// MessageText:
//
// Installed MAPI Version is %1!s!.%0
//
#define AFRES_EXGRT_MAPI_DISPLAY_VERSION ((DWORD)0x400E0013L)

//
// MessageId: AFRES_EXGRT_INST_VOL_REFS_NOTSUPPORT
//
// MessageText:
//
// The Exchange Server installation volume (%1!c!:) on the virtual machine is REFS file system. However, the backup proxy host does not support REFS. As a result, the Exchange Server binaries cannot be processed and the catalog job will fail.%0
//
#define AFRES_EXGRT_INST_VOL_REFS_NOTSUPPORT ((DWORD)0xC00E0014L)

//
// MessageId: AFRES_EXGRT_INST_VOL_NTFSDEDUP_NOTSUPPORT
//
// MessageText:
//
// The Exchange Server installation volume (%1!c!:) on the virtual machine has NTFS Data Deduplication enabled. However, the proxy system does not support the Windows Data Deduplication function. As a result, the Exchange Server binaries cannot be parsed and the catalog job will fail.%0
//
#define AFRES_EXGRT_INST_VOL_NTFSDEDUP_NOTSUPPORT ((DWORD)0xC00E0015L)

//
// MessageId: AFRES_EXGRT_EDB_VOL_REFS_NOTSUPPORT_CAT
//
// MessageText:
//
// The Exchange Server database volume (%1!c!:) on the virtual machine is a REFS file system. However, the backup proxy host does not support REFS. As a result, the Exchange information cannot be parsed and the catalog job will fail.%0
//
#define AFRES_EXGRT_EDB_VOL_REFS_NOTSUPPORT_CAT ((DWORD)0xC00E0016L)

//
// MessageId: AFRES_EXGRT_EDB_VOL_NTFSDEDUP_NOTSUPPORT_CAT
//
// MessageText:
//
// The Exchange Server database volume (%1!c!:) on the virtual machine has NTFS Data Deduplication enabled. However, the proxy system does not support the Windows Data Deduplication function. As a result, the Exchange information cannot be parsed and the catalog job will fail.%0
//
#define AFRES_EXGRT_EDB_VOL_NTFSDEDUP_NOTSUPPORT_CAT ((DWORD)0xC00E0017L)

//
// MessageId: AFRES_EXGRT_EDB_VOL_REFS_NOTSUPPORT_RES
//
// MessageText:
//
// The Exchange Server database volume (%1!c!:) on the virtual machine is a REFS file system. However, the backup proxy host does not support REFS. As a result, the Exchange information cannot be identified and the restore job will fail.%0
//
#define AFRES_EXGRT_EDB_VOL_REFS_NOTSUPPORT_RES ((DWORD)0xC00E0018L)

//
// MessageId: AFRES_EXGRT_EDB_VOL_NTFSDEDUP_NOTSUPPORT_RES
//
// MessageText:
//
// The Exchange Server database volume (%1!c!:) on the virtual machine has NTFS Data Deduplication enabled. However, the proxy system does not support the Windows Data Deduplication function. As a result, the Exchange information cannot be identified and the restore job will fail.%0
//
#define AFRES_EXGRT_EDB_VOL_NTFSDEDUP_NOTSUPPORT_RES ((DWORD)0xC00E0019L)

//
// MessageId: AFRES_EXGRT_HBBUEXCH_OPENMSGSTORE_CERT_ERROR
//
// MessageText:
//
// Failed to communicate with exchange server. It is possible the certificate is not installed on the proxy server. Please ask the administrator to check.%0
//
#define AFRES_EXGRT_HBBUEXCH_OPENMSGSTORE_CERT_ERROR ((DWORD)0xC00E001AL)

//
// MessageId: AFRES_EXGRT_DEFRAG_VOLUME_NOT_ENOUGH
//
// MessageText:
//
// The Exchange database needs to be defragmented before the open operation, but there is not enough available free space in the defragmentation temporary folder (volume:%1!c!:) to proceed. As a result, the defragmentation process may fail.%0
//
#define AFRES_EXGRT_DEFRAG_VOLUME_NOT_ENOUGH ((DWORD)0xC00E001BL)

 // The following are the message definitions for GDDClient
//
// MessageId: IDS_CLTMSG_NETWORK_ERROR
//
// MessageText:
//
// Failed to communicate with deduplication %1!s! on server %2!s!.%0
//
#define IDS_CLTMSG_NETWORK_ERROR         ((DWORD)0xC00F0001L)

//
// MessageId: IDS_CLTMSG_SERVER_ERROR
//
// MessageText:
//
// Unexpected error [%1!d!] from deduplication %2!s! on server %3!s!.%0
//
#define IDS_CLTMSG_SERVER_ERROR          ((DWORD)0xC00F0002L)

//
// MessageId: IDS_CLTMSG_DEDUPE_RETURN_ERROR_FROM_SERVER
//
// MessageText:
//
// An error has occurred from deduplication %1!s! on server %2!s!. Error message = [%3!s!].%0
//
#define IDS_CLTMSG_DEDUPE_RETURN_ERROR_FROM_SERVER ((DWORD)0xC00F0003L)

//
// MessageId: IDS_CLTMSG_DEDUPE_COMM_TO_SERVER_FAILED
//
// MessageText:
//
// Failed to communicate with deduplication %1!s! on server %2!s!. Network error = [%3!s!].%0
//
#define IDS_CLTMSG_DEDUPE_COMM_TO_SERVER_FAILED ((DWORD)0xC00F0004L)

//
// MessageId: IDS_CLTMSG_DEDUPE_INVALID_PARAMETER
//
// MessageText:
//
// Invalid parameter.%0
//
#define IDS_CLTMSG_DEDUPE_INVALID_PARAMETER ((DWORD)0xC00F0005L)

//
// MessageId: IDS_CLTMSG_DEDUPE_INVALID_REQUESTED_OPERATION
//
// MessageText:
//
// The requested operation is invalid.%0
//
#define IDS_CLTMSG_DEDUPE_INVALID_REQUESTED_OPERATION ((DWORD)0xC00F0006L)

//
// MessageId: IDS_CLTMSG_DEDUPE_LOCK_FILE_FAILED
//
// MessageText:
//
// Failed to lock file. The file may currently be in use.%0
//
#define IDS_CLTMSG_DEDUPE_LOCK_FILE_FAILED ((DWORD)0xC00F0007L)

//
// MessageId: IDS_CLTMSG_DEDUPE_SYS_RETURN_ERROR
//
// MessageText:
//
// System call returns an error.%0
//
#define IDS_CLTMSG_DEDUPE_SYS_RETURN_ERROR ((DWORD)0xC00F0008L)

//
// MessageId: IDS_CLTMSG_DEDUPE_SYS_CALL_FAILED
//
// MessageText:
//
// SYSTEM CALL FAILED%0
//
#define IDS_CLTMSG_DEDUPE_SYS_CALL_FAILED ((DWORD)0xC00F0009L)

//
// MessageId: IDS_CLTMSG_DEDUPE_NOT_SUPPOERTED_VERSION
//
// MessageText:
//
// This version of the agent is not supported.%0
//
#define IDS_CLTMSG_DEDUPE_NOT_SUPPOERTED_VERSION ((DWORD)0xC00F000AL)

//
// MessageId: IDS_CLTMSG_DEDUPE_CONFLICTED_JOB_RUNNING
//
// MessageText:
//
// This job is not allowed because of a conflict with another job.%0
//
#define IDS_CLTMSG_DEDUPE_CONFLICTED_JOB_RUNNING ((DWORD)0xC00F000BL)

//
// MessageId: IDS_CLTMSG_DEDUPE_DATA_STORE_CONF_INVALID
//
// MessageText:
//
// This data store configuration is invalid.%0
//
#define IDS_CLTMSG_DEDUPE_DATA_STORE_CONF_INVALID ((DWORD)0xC00F000CL)

//
// MessageId: IDS_CLTMSG_DEDUPE_OPERATION_ON_FILE_FAILED
//
// MessageText:
//
// The operations for this file (create, close, read, write) failed.%0
//
#define IDS_CLTMSG_DEDUPE_OPERATION_ON_FILE_FAILED ((DWORD)0xC00F000DL)

//
// MessageId: IDS_CLTMSG_DEDUPE_UNEXPECTED_DATA_SIZE
//
// MessageText:
//
// Unexpected data size detected from this server.%0
//
#define IDS_CLTMSG_DEDUPE_UNEXPECTED_DATA_SIZE ((DWORD)0xC00F000EL)

//
// MessageId: IDS_CLTMSG_DEDUPE_FAILED_TO_LOAD_ZIP_LIB
//
// MessageText:
//
// FAILED TO LOAD ZIP LIBRARY%0
//
#define IDS_CLTMSG_DEDUPE_FAILED_TO_LOAD_ZIP_LIB ((DWORD)0xC00F000FL)

//
// MessageId: IDS_CLTMSG_DEDUPE_FAILED_TO_COMPRESS
//
// MessageText:
//
// FAILED TO COMPRESS DATA%0
//
#define IDS_CLTMSG_DEDUPE_FAILED_TO_COMPRESS ((DWORD)0xC00F0010L)

//
// MessageId: IDS_CLTMSG_DEDUPE_UNABLE_TO_ACCESS_FILE
//
// MessageText:
//
// Unable to access file.%0
//
#define IDS_CLTMSG_DEDUPE_UNABLE_TO_ACCESS_FILE ((DWORD)0xC00F0011L)

//
// MessageId: IDS_CLTMSG_DEDUPE_OPT_ON_HASH_DB_FAILED
//
// MessageText:
//
// The operations on hash database (search, insert) failed.%0
//
#define IDS_CLTMSG_DEDUPE_OPT_ON_HASH_DB_FAILED ((DWORD)0xC00F0012L)

//
// MessageId: IDS_CLTMSG_DEDUPE_DISK_IS_FULL
//
// MessageText:
//
// Disk is full.%0
//
#define IDS_CLTMSG_DEDUPE_DISK_IS_FULL   ((DWORD)0xC00F0013L)

//
// MessageId: IDS_CLTMSG_DEDUPE_INSUFFICIENT_MEMORY
//
// MessageText:
//
// Insufficient memory.%0
//
#define IDS_CLTMSG_DEDUPE_INSUFFICIENT_MEMORY ((DWORD)0xC00F0014L)

//
// MessageId: IDS_CLTMSG_DEDUPE_HASH_ROLE_IS_INITING
//
// MessageText:
//
// Hash role is initializing.%0
//
#define IDS_CLTMSG_DEDUPE_HASH_ROLE_IS_INITING ((DWORD)0x400F0015L)

//
// MessageId: IDS_CLTMSG_DEDUPE_UNEXPECTED_ERROR
//
// MessageText:
//
// Unexpected error.%0
//
#define IDS_CLTMSG_DEDUPE_UNEXPECTED_ERROR ((DWORD)0xC00F0016L)

//
// MessageId: IDS_CLTMSG_DEDUPE_HASH_SERVER_IS_FULL
//
// MessageText:
//
// Hash server is Full.%0
//
#define IDS_CLTMSG_DEDUPE_HASH_SERVER_IS_FULL ((DWORD)0x400F0017L)

//
// MessageId: IDS_CLTMSG_DEDUPE_UNKNOWN_INTERNAL_ERR
//
// MessageText:
//
// An unknown internal error has occurred.%0
//
#define IDS_CLTMSG_DEDUPE_UNKNOWN_INTERNAL_ERR ((DWORD)0xC00F0018L)

//
// MessageId: IDS_CLTMSG_DEDUPE_BACKUP_SUMARY_DEDUPED
//
// MessageText:
//
// Deduplication reduced the size by %1!d!%2!c!%3!02d!%% to %4!s!.%0
//
#define IDS_CLTMSG_DEDUPE_BACKUP_SUMARY_DEDUPED ((DWORD)0x400F0019L)

//
// MessageId: IDS_CLTMSG_DEDUPE_BACKUP_SUMARY_COMPRESSED_FURTHER
//
// MessageText:
//
// Compression further reduced the size by %1!d!%2!c!%3!02d!%% to %4!s!.%0
//
#define IDS_CLTMSG_DEDUPE_BACKUP_SUMARY_COMPRESSED_FURTHER ((DWORD)0x400F001AL)

//
// MessageId: IDS_CLTMSG_DEDUPE_BACKUP_SUMARY_OVERALL
//
// MessageText:
//
// Total %1!s! written to destination after %2!d!%3!c!%4!02d!%% saving by deduplication and compression.%0
//
#define IDS_CLTMSG_DEDUPE_BACKUP_SUMARY_OVERALL ((DWORD)0x400F001BL)

 // The following are the message definitions for GDDServer
//
// MessageId: IDS_GDDSVR_PRIMARY_ROLE
//
// MessageText:
//
// Index role%0
//
#define IDS_GDDSVR_PRIMARY_ROLE          ((DWORD)0x40100001L)

//
// MessageId: IDS_GDDSVR_HASH_ROLE
//
// MessageText:
//
// Hash role%0
//
#define IDS_GDDSVR_HASH_ROLE             ((DWORD)0x40100002L)

//
// MessageId: IDS_GDDSVR_DATA_ROLE
//
// MessageText:
//
// Data role%0
//
#define IDS_GDDSVR_DATA_ROLE             ((DWORD)0x40100003L)

//
// MessageId: IDS_GDDSVR_INITIALIZED
//
// MessageText:
//
// The %1!s! of "%2!s!": Role has initialized successfully.%0
//
#define IDS_GDDSVR_INITIALIZED           ((DWORD)0x40100004L)

//
// MessageId: IDS_GDDSVR_FILE_INACCESSIBLE
//
// MessageText:
//
// The %1!s! of "%2!s!": %3!s! is not accessible.%0
//
#define IDS_GDDSVR_FILE_INACCESSIBLE     ((DWORD)0xC0100005L)

//
// MessageId: IDS_GDDSVR_CONNT_SHAREFOLDER_FAIL
//
// MessageText:
//
// The %1!s! of "%2!s!": %3!s! is not accessible.%0
//
#define IDS_GDDSVR_CONNT_SHAREFOLDER_FAIL ((DWORD)0xC0100006L)

//
// MessageId: IDS_GDDSVR_LOAD_NETLIB_FAIL
//
// MessageText:
//
// The %1!s! of "%2!s!": Failed to initialize communication library.%0
//
#define IDS_GDDSVR_LOAD_NETLIB_FAIL      ((DWORD)0xC0100007L)

//
// MessageId: IDS_GDDSVR_CREATE_FOLDER_FAIL
//
// MessageText:
//
// The %1!s! of "%2!s!": Failed to create %3!s!. Error=[%4!s!].%0
//
#define IDS_GDDSVR_CREATE_FOLDER_FAIL    ((DWORD)0xC0100008L)

//
// MessageId: IDS_GDDSVR_OPENFILE_FAIL
//
// MessageText:
//
// The %1!s! of "%2!s!": Failed to open %3!s!.Error=[%4!s!].%0
//
#define IDS_GDDSVR_OPENFILE_FAIL         ((DWORD)0xC0100009L)

//
// MessageId: IDS_GDDSVR_INIT_FAIL
//
// MessageText:
//
// The %1!s! of "%2!s!": Failed to initialize.%0
//
#define IDS_GDDSVR_INIT_FAIL             ((DWORD)0xC010000AL)

//
// MessageId: IDS_GDDSVR_INTERNAL_ERROR
//
// MessageText:
//
// The %1!s! of "%2!s!": Unexpected error [%3!d!].%0
//
#define IDS_GDDSVR_INTERNAL_ERROR        ((DWORD)0xC010000BL)

//
// MessageId: IDS_GDDSVR_HASH_CAPACITY_HIGH
//
// MessageText:
//
// The %1!s! of "%2!s!": Data deduplication capacity has reached %3!d!%% of its full capacity. Please increase either the memory or the SSD size to extend the data deduplication capacity.%0
//
#define IDS_GDDSVR_HASH_CAPACITY_HIGH    ((DWORD)0x8010000CL)

//
// MessageId: IDS_GDDSVR_HASH_CAPACITY_FULL
//
// MessageText:
//
// The %1!s! of "%2!s!": Data deduplication capacity has reached its full capacity. Please increase either the memory or the SSD size to extend the data deduplication capacity.%0
//
#define IDS_GDDSVR_HASH_CAPACITY_FULL    ((DWORD)0x8010000DL)

//
// MessageId: IDS_GDDSVR_HAS_BEEN_STARTED
//
// MessageText:
//
// The %1!s! of "%2!s!": Role is already running and and is unable to start again.%0
//
#define IDS_GDDSVR_HAS_BEEN_STARTED      ((DWORD)0xC010000EL)

//
// MessageId: IDS_GDDSVR_STOPPED
//
// MessageText:
//
// The %1!s! of "%2!s!": Role has stopped successfully.%0
//
#define IDS_GDDSVR_STOPPED               ((DWORD)0x4010000FL)

//
// MessageId: IDS_GDDSVR_DISK_FULL
//
// MessageText:
//
// The %1!s! of "%2!s!": %3!s! is full.%0
//
#define IDS_GDDSVR_DISK_FULL             ((DWORD)0x40100010L)

//
// MessageId: IDS_GDDSVR_NO_MEMORY
//
// MessageText:
//
// The %1!s! of "%2!s!": Insufficient memory.%0
//
#define IDS_GDDSVR_NO_MEMORY             ((DWORD)0xC0100011L)

//
// MessageId: IDS_GDDSVR_SERVER_STARTING
//
// MessageText:
//
// The %1!s! of "%2!s!": Role is starting.%0
//
#define IDS_GDDSVR_SERVER_STARTING       ((DWORD)0x40100012L)

//
// MessageId: IDS_GDDSVR_STOPPING
//
// MessageText:
//
// The %1!s! of "%2!s!": Role is stopping.%0
//
#define IDS_GDDSVR_STOPPING              ((DWORD)0x40100013L)

//
// MessageId: IDS_GDDSVR_ABNORMAL_BLOCK_ANY
//
// MessageText:
//
// The %1!s! of "%2!s!": Data store is out of service.%0
//
#define IDS_GDDSVR_ABNORMAL_BLOCK_ANY    ((DWORD)0xC0100014L)

//
// MessageId: IDS_GDDSVR_ABNORMAL_RESTORE_ONLY
//
// MessageText:
//
// The %1!s! of "%2!s!": Data store is experiencing a problem. As a result, only a restore job is allowed.%0
//
#define IDS_GDDSVR_ABNORMAL_RESTORE_ONLY ((DWORD)0xC0100015L)

//
// MessageId: IDS_GDDSVR_BACK_FROM_ABNORMAL
//
// MessageText:
//
// The %1!s! of "%2!s!": Data store has been recovered.%0
//
#define IDS_GDDSVR_BACK_FROM_ABNORMAL    ((DWORD)0x40100016L)

//
// MessageId: IDS_GDDSVR_OPERATION_ON_FILE_FAIL
//
// MessageText:
//
// The %1!s! of "%2!s!": The operations (create, close, read, and write) failed for this file.%0
//
#define IDS_GDDSVR_OPERATION_ON_FILE_FAIL ((DWORD)0xC0100017L)

//
// MessageId: IDS_GDDSVR_PHY_MEM_IS_LESS_THAN_SETTING
//
// MessageText:
//
// The %1!s! of "%2!s!": The configured memory size is bigger than the total physical memory. It might run out of memory later.%0
//
#define IDS_GDDSVR_PHY_MEM_IS_LESS_THAN_SETTING ((DWORD)0x80100018L)

//
// MessageId: IDS_GDDSVR_SERVER_BUSY_HANG_PURGE
//
// MessageText:
//
// Data store [%1!s!] is busy to run regular UDP jobs. The total suspended time in current cycle of purge and disk reclamation is more than [%2!d!] days due to active regular UDP jobs. Please either reschedule UDP jobs to mitigate workload of the data store or run command line tool “as_gddmgr.exe –Purge Start” to enable the purge in parallel. Please be aware that running purge in parallel might cause throughput degradation of regular UDP jobs.%0
//
#define IDS_GDDSVR_SERVER_BUSY_HANG_PURGE ((DWORD)0x80100019L)

//
// MessageId: IDS_GDDSVR_SERVER_BUSY_RUN_PURGE_PARALLEL
//
// MessageText:
//
// Data store [%1!s!] is busy to run regular UDP jobs. The total suspended time in current cycle of purge and disk reclamation is more than [%2!d!] days due to active regular UDP jobs. Purge in parallel has been automatically enabled so that disk space of the obsoleted data blocks could be freed. Please be aware that running purge in parallel might cause throughput degradation of regular UDP jobs.%0
//
#define IDS_GDDSVR_SERVER_BUSY_RUN_PURGE_PARALLEL ((DWORD)0x8010001AL)

 // The following are the message definitions for InstantVM
//
// MessageId: INSTANT_VM_PRECHECK_DATACENTER_NOT_EXIST
//
// MessageText:
//
// The target datacenter [%1!s!] does not exist.%0
//
#define INSTANT_VM_PRECHECK_DATACENTER_NOT_EXIST ((DWORD)0xC01B0001L)

//
// MessageId: INSTANT_VM_PRECHECK_ESXHOST_NOT_EXIST
//
// MessageText:
//
// The target ESX(i) host [%1!s!] does not exist.%0
//
#define INSTANT_VM_PRECHECK_ESXHOST_NOT_EXIST ((DWORD)0xC01B0002L)

//
// MessageId: INSTANT_VM_PRECHECK_RESOURCE_POOL_NOT_EXIST
//
// MessageText:
//
// The target resource pool [%1!s!] does not exist. As a result, the default resource pool will be automatically selected.%0
//
#define INSTANT_VM_PRECHECK_RESOURCE_POOL_NOT_EXIST ((DWORD)0x801B0003L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_VERIFY_HYPERVISOR_INFO
//
// MessageText:
//
// Failed to verify hypervisor information. Error Message: %1!s! %0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_VERIFY_HYPERVISOR_INFO ((DWORD)0xC01B0004L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CONNECT
//
// MessageText:
//
// Failed to connect to the remote path [%1!s!] from host [%2!s!]. Please check if the remote path, username, and password are correct.%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CONNECT ((DWORD)0xC01B0005L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_GET_ADR_CONFIG
//
// MessageText:
//
// Failed to get configuration file [%1!s!] for node [%2!s!].%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_GET_ADR_CONFIG ((DWORD)0xC01B0006L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_GET_BACKUP_INFO
//
// MessageText:
//
// Failed to get backup information file [%1!s!] for node [%2!s!].%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_GET_BACKUP_INFO ((DWORD)0xC01B0007L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CHECK_OS
//
// MessageText:
//
// The target hypervisor [%1!s!] does not support the operating system of the source machine [%2!s!].%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CHECK_OS ((DWORD)0xC01B0008L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CHECK_DISK_SIZE
//
// MessageText:
//
// The target hypervisor [%1!s!] does not support a disk size equal to or larger than 2 TB.%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CHECK_DISK_SIZE ((DWORD)0xC01B0009L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CHECK_CPU
//
// MessageText:
//
// The CPU number [%1!d!] is greater than the maximum number supported on hypervisor [%2!s!]. The CPU number will be automatically modified to the maximum value of [%3!d!].%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CHECK_CPU ((DWORD)0x801B000AL)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CHECK_MEMORY
//
// MessageText:
//
// The memory size [%1!d!] is greater than the maximum size supported on hypervisor [%2!s!]. The memory size will be automatically modified to the maximum value of [%3!d!].%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CHECK_MEMORY ((DWORD)0x801B000BL)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CHECK_64BIT
//
// MessageText:
//
// The target hypervisor [%1!s!] does not support a guest virtual machine with a 64-bit operating system.%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CHECK_64BIT ((DWORD)0xC01B000CL)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CHECK_VM_NAME_EXIST
//
// MessageText:
//
// The virtual machine name [%1!s!] already exists. Use name [%2!s!] instead.%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CHECK_VM_NAME_EXIST ((DWORD)0x801B000DL)

//
// MessageId: INSTANT_VM_ERROR_FEATURE_NOT_INSTALLED
//
// MessageText:
//
// You need to install the Windows Server Role for Network File System (NFS) on the recovery server: [%1!s!]. Please install this functionality and retry.?%0
//
#define INSTANT_VM_ERROR_FEATURE_NOT_INSTALLED ((DWORD)0xC01B000EL)

//
// MessageId: INSTANT_VM_FAILED_START_NFS
//
// MessageText:
//
// Failed to start the NFS service.%0 
//
#define INSTANT_VM_FAILED_START_NFS      ((DWORD)0xC01B000FL)

//
// MessageId: INSTANT_VM_FAILED_WMI_PROVIDER
//
// MessageText:
//
// Failed to connect to the Windows WMI Network File System Service Provider. Error Message: %1!s!%0
//
#define INSTANT_VM_FAILED_WMI_PROVIDER   ((DWORD)0xC01B0010L)

//
// MessageId: INSTANT_VM_START_NFS_SERVICE
//
// MessageText:
//
// Starting the NFS service.%0 
//
#define INSTANT_VM_START_NFS_SERVICE     ((DWORD)0x401B0011L)

//
// MessageId: INSTANT_VM_FAILED_RETRIEVE_NFS_SHARES
//
// MessageText:
//
// Failed to retrieve the NFS share list. Error code: %1!d!.%0
//
#define INSTANT_VM_FAILED_RETRIEVE_NFS_SHARES ((DWORD)0xC01B0012L)

//
// MessageId: INSTANT_VM_FAILED_CREATE_NFS_SHARE
//
// MessageText:
//
// Failed to create the NFS share [%1!s!] for the local path [%2!s!].%0 
//
#define INSTANT_VM_FAILED_CREATE_NFS_SHARE ((DWORD)0xC01B0013L)

//
// MessageId: INSTANT_VM_FAILED_UPDATE_NFS_SHARE
//
// MessageText:
//
// Failed to update the permissions for the NFS share [%1!s!].%0
//
#define INSTANT_VM_FAILED_UPDATE_NFS_SHARE ((DWORD)0xC01B0014L)

//
// MessageId: INSTANT_VM_FAILED_UPDATE_NFS_FW
//
// MessageText:
//
// Failed to update the Inbound Rules assigned to the NFS server.%0
//
#define INSTANT_VM_FAILED_UPDATE_NFS_FW  ((DWORD)0xC01B0015L)

//
// MessageId: INSTANT_VM_FAILED_CREATE_NFS_DATASTORRE
//
// MessageText:
//
// Failed to create NFS-based datastore [%1!s!] with the NFS Share Name [%2!s!]. Error code: %3!d!. Error message: %4!s! (details: %5!s!)%0
//
#define INSTANT_VM_FAILED_CREATE_NFS_DATASTORRE ((DWORD)0xC01B0016L)

//
// MessageId: INSTANT_VM_START_CREATE_BY_IP
//
// MessageText:
//
// Will reattempt to create NFS-based datastore [%1!s!] on the NFS Share Name [%2!s!], but this time using server IP address [%3!s!].%0
//
#define INSTANT_VM_START_CREATE_BY_IP    ((DWORD)0x401B0017L)

//
// MessageId: INSTANT_VM_ERROR_NAME_CONFLICT
//
// MessageText:
//
// The NFS share name [%1!s!] already exists at [%2!s!].%0
//
#define INSTANT_VM_ERROR_NAME_CONFLICT   ((DWORD)0xC01B0018L)

//
// MessageId: INSTANT_VM_FAILED_TO_GET_IP_ADDRESS
//
// MessageText:
//
// Failed to get the IP address of the host [%1!s!].%0 
//
#define INSTANT_VM_FAILED_TO_GET_IP_ADDRESS ((DWORD)0xC01B0019L)

//
// MessageId: INSTANT_VM_PASS_PRECHECK
//
// MessageText:
//
// Successfully completed the pre-check for node: [%1!s!].%0
//
#define INSTANT_VM_PASS_PRECHECK         ((DWORD)0x401B001AL)

//
// MessageId: INSTANT_VM_CREATE_NFS_DATASTORE
//
// MessageText:
//
// Datastore [%1!s!] for VMware was successfully created on ESX(i) host [%2!s!].%0 
//
#define INSTANT_VM_CREATE_NFS_DATASTORE  ((DWORD)0x401B001BL)

//
// MessageId: INSTANT_VM_CREATED
//
// MessageText:
//
// Successfully created virtual machine [%1!s!].%0
//
#define INSTANT_VM_CREATED               ((DWORD)0x401B001CL)

//
// MessageId: INSTANT_VM_SNAPSHOT_CREATED
//
// MessageText:
//
// Successfully created snapshot for virtual machine [%1!s!].%0
//
#define INSTANT_VM_SNAPSHOT_CREATED      ((DWORD)0x401B001DL)

//
// MessageId: INSTANT_VM_POWERED_ON
//
// MessageText:
//
// Successfully powered-on virtual machine [%1!s!].%0
//
#define INSTANT_VM_POWERED_ON            ((DWORD)0x401B001EL)

//
// MessageId: INSTANT_VM_POWERED_OFF
//
// MessageText:
//
// Successfully powered-off virtual machine [%1!s!].%0
//
#define INSTANT_VM_POWERED_OFF           ((DWORD)0x401B001FL)

//
// MessageId: INSTANT_VM_DELETED
//
// MessageText:
//
// Successfully deleted virtual machine [%1!s!].%0
//
#define INSTANT_VM_DELETED               ((DWORD)0x401B0020L)

//
// MessageId: INSTANT_VM_FAILED_GET_BOOT_AND_SYS_DISK_SIG
//
// MessageText:
//
// The backup session does not contain a boot disk or a system disk.%0
//
#define INSTANT_VM_FAILED_GET_BOOT_AND_SYS_DISK_SIG ((DWORD)0xC01B0021L)

//
// MessageId: INSTANT_VM_DRIVER_VERSION
//
// MessageText:
//
// The version of the driver file [%1!s!] is [%2!s!].%0
//
#define INSTANT_VM_DRIVER_VERSION        ((DWORD)0x401B0022L)

//
// MessageId: INSTANT_VM_DRIVER_NOT_INSTALLED
//
// MessageText:
//
// The driver [%1!s!] has not been installed. Please run the following command to install the driver manually: [InstantVMDrvInstall.exe -i %2!s!] %0
//
#define INSTANT_VM_DRIVER_NOT_INSTALLED  ((DWORD)0xC01B0023L)

//
// MessageId: INSTANT_VM_VM_ON_ENCRYPTED_FOLDER
//
// MessageText:
//
// Unable to create virtual disk images in folder [%1!s!] because the folder has encryption enabled.%0
//
#define INSTANT_VM_VM_ON_ENCRYPTED_FOLDER ((DWORD)0xC01B0024L)

//
// MessageId: INSTANT_VM_VM_ON_COMPRESSED_FOLDER
//
// MessageText:
//
// Unable to create virtual disk images in folder [%1!s!] because the folder has compression enabled.%0
//
#define INSTANT_VM_VM_ON_COMPRESSED_FOLDER ((DWORD)0xC01B0025L)

//
// MessageId: INSTANT_VM_ON_PROVIDER_CRITICAL_ERROR
//
// MessageText:
//
// Failed to process the virtual disk file [%1!s!] with the disk GUID [%2!s!]. Error message: %3!s!%0 
//
#define INSTANT_VM_ON_PROVIDER_CRITICAL_ERROR ((DWORD)0xC01B0026L)

//
// MessageId: INSTANT_VM_PRECHECK_UEFI_NOT_SUPPORT
//
// MessageText:
//
// The target hypervisor [%1!s!] does not support the source machine [%2!s!] booting from the EFI partition.%0
//
#define INSTANT_VM_PRECHECK_UEFI_NOT_SUPPORT ((DWORD)0xC01B0027L)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_CONNECT_HYPERVISOR
//
// MessageText:
//
// Failed to connect to the target hypervisor [%1!s!]. Error message: %2!s!%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_CONNECT_HYPERVISOR ((DWORD)0xC01B0028L)

//
// MessageId: INSTANT_VM_PRECHECK_CLUSTER_NOT_EXIST
//
// MessageText:
//
// Target cluster [%1!s!] does not exist.%0
//
#define INSTANT_VM_PRECHECK_CLUSTER_NOT_EXIST ((DWORD)0xC01B0029L)

//
// MessageId: INSTANT_VM_SNAPSHOT_CREATED_FAILED
//
// MessageText:
//
// Failed to create snapshot for virtual machine [%1!s!]. Error message: %2!s!%0
//
#define INSTANT_VM_SNAPSHOT_CREATED_FAILED ((DWORD)0xC01B002AL)

//
// MessageId: INSTANT_VM_POWERED_ON_FAILED
//
// MessageText:
//
// Failed to power on virtual machine [%1!s!]Please power on the virtual machine manually. Error message: %2!s!%0
//
#define INSTANT_VM_POWERED_ON_FAILED     ((DWORD)0xC01B002BL)

//
// MessageId: INSTANT_VM_POWERED_OFF_FAILED
//
// MessageText:
//
// Failed to power off virtual machine [%1!s!]. Error message: %2!s!%0
//
#define INSTANT_VM_POWERED_OFF_FAILED    ((DWORD)0xC01B002CL)

//
// MessageId: INSTANT_VM_DELETED_FAILED
//
// MessageText:
//
// Failed to delete virtual machine [%1!s!]. Error message: %2!s!%0
//
#define INSTANT_VM_DELETED_FAILED        ((DWORD)0xC01B002DL)

//
// MessageId: INSTANT_VM_RECONFIGURATION_FAILED
//
// MessageText:
//
// Failed to re-configure virtual machine [%1!s!]. Error message: %2!s!%0
//
#define INSTANT_VM_RECONFIGURATION_FAILED ((DWORD)0xC01B002EL)

//
// MessageId: INSTANT_VM_CANCEL_IVM_JOB
//
// MessageText:
//
// Cancel the %1!s! job.%0
//
#define INSTANT_VM_CANCEL_IVM_JOB        ((DWORD)0x401B002FL)

//
// MessageId: ERROR_INSTANT_VM_AGENT_CLUSTER_NO_VALID_HOST
//
// MessageText:
//
// The cluster [%1!s!] does not contain a suitable ESX/ESXi host to start the virtual machine.%0
//
#define ERROR_INSTANT_VM_AGENT_CLUSTER_NO_VALID_HOST ((DWORD)0xC01B0030L)

//
// MessageId: INSTANT_VM_AGENT_CLUSTER_FIND_HOST
//
// MessageText:
//
// Virtual machine [%1!s!] will be created on the ESX/ESXi host [%2!s!].%0
//
#define INSTANT_VM_AGENT_CLUSTER_FIND_HOST ((DWORD)0x401B0031L)

//
// MessageId: WARNING_INSTANT_VM_AGENT_CORRECT_MEMORY
//
// MessageText:
//
// The memory size should be in multiples of 2MB (for Hyper-V) or 4MB (for VMware). As a result, the memory size has been adjusted to [%1!d!]MB.%0
//
#define WARNING_INSTANT_VM_AGENT_CORRECT_MEMORY ((DWORD)0x801B0032L)

//
// MessageId: INSTANT_VM_AGENT_CHANGED_UDP_AGENT_STARTUPTYPE
//
// MessageText:
//
// To avoid the backup job to run inside the virtual machine and back up the unnecessary data, the startup type of ^AU_ProductName_AGENT^ service is changed to Manual inside the virtual machine. To enable the service you have to manually start it up.%0
//
#define INSTANT_VM_AGENT_CHANGED_UDP_AGENT_STARTUPTYPE ((DWORD)0x801B0033L)

//
// MessageId: INSTANT_VM_NO_NEED_TO_POWER_ON
//
// MessageText:
//
// Based upon the task configuration, the power on process will be skipped for virtual machine: [%1!s!].%0
//
#define INSTANT_VM_NO_NEED_TO_POWER_ON   ((DWORD)0x401B0034L)

//
// MessageId: ERROR_INSTANT_NFS_SHARE_FOLDER_EMBEDED
//
// MessageText:
//
// The NFS share folder [%1!s!] cannot be included in or include another existing NFS share folder.%0
//
#define ERROR_INSTANT_NFS_SHARE_FOLDER_EMBEDED ((DWORD)0xC01B0035L)

//
// MessageId: INSTANT_VM_CHECK_MOVED
//
// MessageText:
//
// Checking if virtual machine [%1!s!] was moved.%0
//
#define INSTANT_VM_CHECK_MOVED           ((DWORD)0x401B0036L)

//
// MessageId: INSTANT_VM_SKIP_DELETING
//
// MessageText:
//
// Virtual machine [%1!s!] was moved. Skip deleting virtual machine.%0
//
#define INSTANT_VM_SKIP_DELETING         ((DWORD)0x801B0037L)

//
// MessageId: INSTANT_VM_STARTING_IVM_JOB
//
// MessageText:
//
// Starting the %1!s! job. Process ID [%2!s!]. The log file is [%3!s!].%0
//
#define INSTANT_VM_STARTING_IVM_JOB      ((DWORD)0x401B0038L)

//
// MessageId: INSTANT_VM_JOB_DESC_SOURCE_NODE
//
// MessageText:
//
// The source node is [%1!s!], the backup destination is [%2!s!], and the recovery point is [%3!d!].%0
//
#define INSTANT_VM_JOB_DESC_SOURCE_NODE  ((DWORD)0x401B0039L)

//
// MessageId: INSTANT_VM_JOB_DESC_HYPERVISOR_TYPE_HYPERV
//
// MessageText:
//
// Microsoft Hyper-V Server%0
//
#define INSTANT_VM_JOB_DESC_HYPERVISOR_TYPE_HYPERV ((DWORD)0x401B003AL)

//
// MessageId: INSTANT_VM_JOB_DESC_HYPERVISOR_TYPE_VMWARE
//
// MessageText:
//
// VMware vCenter/ESX(i) Server%0
//
#define INSTANT_VM_JOB_DESC_HYPERVISOR_TYPE_VMWARE ((DWORD)0x401B003BL)

//
// MessageId: INSTANT_VM_JOB_DESC_HYPERVISOR
//
// MessageText:
//
// The target hypervisor type is [%1!s!], and the server name is [%2!s!].%0
//
#define INSTANT_VM_JOB_DESC_HYPERVISOR   ((DWORD)0x401B003CL)

//
// MessageId: INSTANT_VM_JOB_CONNECT_HYPERVISOR
//
// MessageText:
//
// Successfully connected to the target hypervisor [%1!s!].%0
//
#define INSTANT_VM_JOB_CONNECT_HYPERVISOR ((DWORD)0x401B003DL)

//
// MessageId: INSTANT_VM_JOB_CONNECT_REMOTE_PATH
//
// MessageText:
//
// Successfully connected to the remote path [%1!s!].%0
//
#define INSTANT_VM_JOB_CONNECT_REMOTE_PATH ((DWORD)0x401B003EL)

//
// MessageId: INSTANT_VM_JOB_LOCK_BACKUP_SESSION
//
// MessageText:
//
// Successfully locked the backup session on [%1!s!].%0
//
#define INSTANT_VM_JOB_LOCK_BACKUP_SESSION ((DWORD)0x401B003FL)

//
// MessageId: INSTANT_VM_JOB_UNLOCK_BACKUP_SESSION
//
// MessageText:
//
// Unlock the backup session on [%1!s!].%0
//
#define INSTANT_VM_JOB_UNLOCK_BACKUP_SESSION ((DWORD)0x401B0040L)

//
// MessageId: INSTANT_VM_JOB_VERIFY_HYPERVISOR
//
// MessageText:
//
// Successfully verified the hypervisor server information.%0
//
#define INSTANT_VM_JOB_VERIFY_HYPERVISOR ((DWORD)0x401B0041L)

//
// MessageId: INSTANT_VM_JOB_CHECK_NFS_DATASTORE
//
// MessageText:
//
// Checking if the datastore with the NFS share [%1!s!] already exists on the ESX(i) host [%2!s!].%0
//
#define INSTANT_VM_JOB_CHECK_NFS_DATASTORE ((DWORD)0x401B0042L)

//
// MessageId: INSTANT_VM_JOB_USE_EXISTING_NFS_DATASTORE
//
// MessageText:
//
// The datastore [%1!s!] with the NFS share [%2!s!] already exists. The existing one will be used.%0
//
#define INSTANT_VM_JOB_USE_EXISTING_NFS_DATASTORE ((DWORD)0x401B0043L)

//
// MessageId: INSTANT_VM_JOB_INACTIVE_EXISTING_NFS_DATASTORE
//
// MessageText:
//
// The datastore [%1!s!] with the NFS share [%2!s!] is in an inactive state. Try to dismount it.%0
//
#define INSTANT_VM_JOB_INACTIVE_EXISTING_NFS_DATASTORE ((DWORD)0x801B0044L)

//
// MessageId: INSTANT_VM_JOB_MOUNTING_VIRTUAL_DISK
//
// MessageText:
//
// Mounting virtual disk file(s) to update the disk information and boot configuration.%0
//
#define INSTANT_VM_JOB_MOUNTING_VIRTUAL_DISK ((DWORD)0x401B0045L)

//
// MessageId: INSTANT_VM_JOB_MOUNT_VIRTUAL_DISK_FAILED
//
// MessageText:
//
// Failed to mount virtual disk file(s). Error code: %1!d!. Error message: %2!s!%0
//
#define INSTANT_VM_JOB_MOUNT_VIRTUAL_DISK_FAILED ((DWORD)0xC01B0046L)

//
// MessageId: INSTANT_VM_JOB_UNMOUNT_VIRTUAL_DISK
//
// MessageText:
//
// Successfully dismounted the virtual disk file(s).%0
//
#define INSTANT_VM_JOB_UNMOUNT_VIRTUAL_DISK ((DWORD)0x401B0047L)

//
// MessageId: INSTANT_VM_JOB_STARTED
//
// MessageText:
//
// Successfully started the %1!s! job. Waiting for user actions.%0
//
#define INSTANT_VM_JOB_STARTED           ((DWORD)0x401B0048L)

//
// MessageId: INSTANT_VM_JOB_FAILED
//
// MessageText:
//
// Failed to successfully complete %1!s! job.%0
//
#define INSTANT_VM_JOB_FAILED            ((DWORD)0xC01B0049L)

//
// MessageId: INSTANT_VM_JOB_STOPPING
//
// MessageText:
//
// Stopping the %1!s! job.%0
//
#define INSTANT_VM_JOB_STOPPING          ((DWORD)0x401B004AL)

//
// MessageId: INSTANT_VM_JOB_STOPPED
//
// MessageText:
//
// The %1!s! job is stopped.%0
//
#define INSTANT_VM_JOB_STOPPED           ((DWORD)0x401B004BL)

//
// MessageId: INSTANT_VM_JOB_STOPPING_FAILED
//
// MessageText:
//
// Failed to stop the %1!s! job.%0
//
#define INSTANT_VM_JOB_STOPPING_FAILED   ((DWORD)0xC01B004CL)

//
// MessageId: INSTANT_VM_HOST_IS_CLUSTERED
//
// MessageText:
//
// Hyper-V host %1!s! is cluster-enabled.%0
//
#define INSTANT_VM_HOST_IS_CLUSTERED     ((DWORD)0x401B004DL)

//
// MessageId: ERROR_INSTANT_VM_NOT_CLUSTER_SHAREDVOLUME
//
// MessageText:
//
// The path %1!s! where the virtual machine configuration is stored is not a path to a storage location in the cluster or to a storage location that can be added to the cluster. To make this virtual machine highly available, please verify that this storage location is available to every node in the cluster.%0
//
#define ERROR_INSTANT_VM_NOT_CLUSTER_SHAREDVOLUME ((DWORD)0x801B004EL)

//
// MessageId: ERROR_INSTANT_VM_ADD_CLUSTER_GROUP
//
// MessageText:
//
// Failed to create cluster group %1!s! for virtual machine %2!s!.%0
//
#define ERROR_INSTANT_VM_ADD_CLUSTER_GROUP ((DWORD)0xC01B004FL)

//
// MessageId: INSTANT_VM_ADD_CLUSTER_GROUP
//
// MessageText:
//
// Virtual machine %1!s! was successfully added to cluster group %2!s!.%0
//
#define INSTANT_VM_ADD_CLUSTER_GROUP     ((DWORD)0x401B0050L)

//
// MessageId: ERROR_INSTANT_VM_TAKE_OFFLINE_CLUSTER_GROUP
//
// MessageText:
//
// Failed to take cluster group %1!s! of virtual machine %2!s! offline. %0
//
#define ERROR_INSTANT_VM_TAKE_OFFLINE_CLUSTER_GROUP ((DWORD)0xC01B0051L)

//
// MessageId: INSTANT_VM_TAKE_OFFLINE_CLUSTER_GROUP
//
// MessageText:
//
// Cluster group %1!s! of virtual machine %2!s! successfully taken offline.%0
//
#define INSTANT_VM_TAKE_OFFLINE_CLUSTER_GROUP ((DWORD)0x401B0052L)

//
// MessageId: INSTANT_VM_PRECHECK_CLUSTER_ESXHOST_NOT_EXIST
//
// MessageText:
//
// The target ESX(i) host [%1!s!] does not exist. As a result, a random ESX(i) host on the cluster [%2!s!] will be selected.%0
//
#define INSTANT_VM_PRECHECK_CLUSTER_ESXHOST_NOT_EXIST ((DWORD)0x801B0053L)

//
// MessageId: INSTANT_VM_SNAPSHOT_NAME
//
// MessageText:
//
// arcserve ^AU_ProductName_IVM_SHORT^ snapshot%0
//
#define INSTANT_VM_SNAPSHOT_NAME         ((DWORD)0x401B0054L)

//
// MessageId: ASSURANCE_RECOVERY_SNAPSHOT_NAME
//
// MessageText:
//
// arcserve ^AU_ProductName_AR_SHORT^ snapshot%0
//
#define ASSURANCE_RECOVERY_SNAPSHOT_NAME ((DWORD)0x401B0055L)

//
// MessageId: INSTANT_VM_UNMOUNT_NFS_DATASTORRE
//
// MessageText:
//
// Successfully dismounted the datastore [%1!s!].%0
//
#define INSTANT_VM_UNMOUNT_NFS_DATASTORRE ((DWORD)0x401B0056L)

//
// MessageId: INSTANT_VM_FAILED_UNMOUNT_NFS_DATASTORRE
//
// MessageText:
//
// Failed to dismount the datastore [%1!s!]. Error code: %2!d!. Error message: %3!s!%0 
//
#define INSTANT_VM_FAILED_UNMOUNT_NFS_DATASTORRE ((DWORD)0x801B0057L)

//
// MessageId: ERROR_INSTANT_VM_AGENT_CRASH
//
// MessageText:
//
// The previous job is crashed. Try to remove virtual machine [%1!s!].%0
//
#define ERROR_INSTANT_VM_AGENT_CRASH     ((DWORD)0xC01B0058L)

//
// MessageId: WARNING_INSTANT_VM_PRECHECK_DISK_SPACE
//
// MessageText:
//
// The current free space on volume %1!s! is [%2!s! GB]. As a result, there may not be enough space to save all the virtual disks [%3!s! GB] and the memory swap file [%4!s! GB] if the virtual disks are filled inside the virtual machine.%0
//
#define WARNING_INSTANT_VM_PRECHECK_DISK_SPACE ((DWORD)0x801B0059L)

//
// MessageId: ERROR_INSTANT_VM_TAKEONLINE_CLUSTER_GROUP
//
// MessageText:
//
// Failed to take cluster group %1!s! online.%0
//
#define ERROR_INSTANT_VM_TAKEONLINE_CLUSTER_GROUP ((DWORD)0xC01B005AL)

//
// MessageId: INSTANT_VM_TAKEONLINE_CLUSTER_GROUP
//
// MessageText:
//
// Cluster group %1!s! was successfully taken online.%0
//
#define INSTANT_VM_TAKEONLINE_CLUSTER_GROUP ((DWORD)0x401B005BL)

//
// MessageId: WARNING_INSTANT_VM_NOT_DOMAIN_ACCOUNT
//
// MessageText:
//
// The current user account %1!s! is not a domain account. As a result, no clustered virtual machine group will be created.%0
//
#define WARNING_INSTANT_VM_NOT_DOMAIN_ACCOUNT ((DWORD)0x801B005CL)

//
// MessageId: ERROR_INSTANT_VM_GET_ADMINISTRATOR_ACCOUNT
//
// MessageText:
//
// Failed to get the administrator account of host %1!s!.%0
//
#define ERROR_INSTANT_VM_GET_ADMINISTRATOR_ACCOUNT ((DWORD)0xC01B005DL)

//
// MessageId: ERROR_INSTANT_VM_AGENT_PROXY_64BIT
//
// MessageText:
//
// The recovery server must be a 64-bit Windows server operating system (Windows Server 2008 R2 or later). The current recovery server [%1!s!] is %2!d!-bit %3!s!.%0
//
#define ERROR_INSTANT_VM_AGENT_PROXY_64BIT ((DWORD)0xC01B005EL)

//
// MessageId: INSTANT_VM_PRECHECK_VERIFY_VCLOUD_INFO
//
// MessageText:
//
// Successfully verified the vCloud server information.%0
//
#define INSTANT_VM_PRECHECK_VERIFY_VCLOUD_INFO ((DWORD)0x401B005FL)

//
// MessageId: INSTANT_VM_PRECHECK_FAIL_TO_VERIFY_VCLOUD_INFO
//
// MessageText:
//
// Failed to verify the vCloud server information. Error Message: %1!s!.%0
//
#define INSTANT_VM_PRECHECK_FAIL_TO_VERIFY_VCLOUD_INFO ((DWORD)0xC01B0060L)

//
// MessageId: INSTANT_VM_PRECHECK_VCLOUD_ORGANIZATION_NOT_EXIST
//
// MessageText:
//
// The target Organization [%1!s!] does not exist.%0
//
#define INSTANT_VM_PRECHECK_VCLOUD_ORGANIZATION_NOT_EXIST ((DWORD)0xC01B0061L)

//
// MessageId: INSTANT_VM_PRECHECK_VCLOUD_ORGANIZATION_VDC_NOT_EXIST
//
// MessageText:
//
// The target Organization VDC [%1!s!] does not exist.%0
//
#define INSTANT_VM_PRECHECK_VCLOUD_ORGANIZATION_VDC_NOT_EXIST ((DWORD)0xC01B0062L)

//
// MessageId: INSTANT_VM_PRECHECK_VCLOUD_VAPP_NOT_EXIST
//
// MessageText:
//
// The target vApp [%1!s!] does not exist.%0
//
#define INSTANT_VM_PRECHECK_VCLOUD_VAPP_NOT_EXIST ((DWORD)0xC01B0063L)

//
// MessageId: INSTANT_VM_START_CHECK_VM_HEARTBEAT
//
// MessageText:
//
// Start to check heartbeat of virtual machine: %1!s!.%0
//
#define INSTANT_VM_START_CHECK_VM_HEARTBEAT ((DWORD)0x401B0064L)

//
// MessageId: ERROR_INSTANT_VM_UPDATE_VM_HEARTBEAT_STATUS
//
// MessageText:
//
// Failed to update heartbeat status of virtual machine: %1!s!.%0
//
#define ERROR_INSTANT_VM_UPDATE_VM_HEARTBEAT_STATUS ((DWORD)0xC01B0065L)

//
// MessageId: ERROR_INSTANT_VM_CHECK_VM_HEARTBEAT_STATUS
//
// MessageText:
//
// The heartbeat of virtual machine %1!s! is not correct. Please verify if the virtual machine integration tools are correctly installed.%0
//
#define ERROR_INSTANT_VM_CHECK_VM_HEARTBEAT_STATUS ((DWORD)0xC01B0066L)

//
// MessageId: INSTANT_VM_CHECK_VM_HEARTBEAT_OK
//
// MessageText:
//
// The heartbeat of virtual machine %1!s! is correct.%0
//
#define INSTANT_VM_CHECK_VM_HEARTBEAT_OK ((DWORD)0x401B0067L)

//
// MessageId: INSTANT_VM_ALIVE_CHECK_VM_HEARTBEAT_DISABLED
//
// MessageText:
//
// The heartbeat check of virtual machine %1!s! is disabled.%0
//
#define INSTANT_VM_ALIVE_CHECK_VM_HEARTBEAT_DISABLED ((DWORD)0x401B0068L)

//
// MessageId: ERROR_INSTANT_VM_RUN_CUSTOM_SCRIPT_FAILED
//
// MessageText:
//
// Failed to run custom script (script path is %1!s!). Error: %2!d!.%0
//
#define ERROR_INSTANT_VM_RUN_CUSTOM_SCRIPT_FAILED ((DWORD)0xC01B0069L)

//
// MessageId: INSTANT_VM_RUN_CUSTOM_SCRIPT_OK
//
// MessageText:
//
// Successfully completed custom script (script path is %1!s!).%0
//
#define INSTANT_VM_RUN_CUSTOM_SCRIPT_OK  ((DWORD)0x401B006AL)

//
// MessageId: INSTANT_VM_CREATED_FAILED
//
// MessageText:
//
// Failed to create virtual machine [%1!s!]. Error: %2!s!.%0
//
#define INSTANT_VM_CREATED_FAILED        ((DWORD)0xC01B006BL)

//
// MessageId: INSTANT_VM_SOURCE_MACHINE_OS_INFORMATION
//
// MessageText:
//
// The Windows edition of source node [%1!s!] is [%2!s!]. System type: (%3!d!)-bit Operating System. Firmware: %4!s!. CPU Count: %5!d!. Memory size: %6!d! MB.%0
//
#define INSTANT_VM_SOURCE_MACHINE_OS_INFORMATION ((DWORD)0x401B006CL)

//
// MessageId: ERROR_INSTANT_VM_CHECK_FILE_SYSTEM_NOT_NTFS
//
// MessageText:
//
// The volume [%1!s!] file system is %2!s!. As a result, it does not support creation of a NFS share folder. The NFS share folder must be created on a NTFS volume.%0
//
#define ERROR_INSTANT_VM_CHECK_FILE_SYSTEM_NOT_NTFS ((DWORD)0xC01B006DL)

//
// MessageId: ERROR_INSTANT_VM_CHECK_FILE_SYSTEM_UNKNOWN
//
// MessageText:
//
// The volume [%1!s!] file system is unknown. Please check if the volume is a floppy disk.%0
//
#define ERROR_INSTANT_VM_CHECK_FILE_SYSTEM_UNKNOWN ((DWORD)0xC01B006EL)

//
// MessageId: ERROR_INSTANT_VM_FAIL_CHECK_FILE_SYSTEM
//
// MessageText:
//
// Failed to check file system of volume [%1!s!]. Error message: %2!s!%0
//
#define ERROR_INSTANT_VM_FAIL_CHECK_FILE_SYSTEM ((DWORD)0xC01B006FL)

//
// MessageId: INSTANT_VM_SKIP_DELETING_HYDRATED
//
// MessageText:
//
// Virtual machine [%1!s!] was hydrated. Skip deleting virtual machine.%0
//
#define INSTANT_VM_SKIP_DELETING_HYDRATED ((DWORD)0x801B0070L)

//
// MessageId: ERROR_INSTANT_VM_UPDATE_EXEC_SCRIPT_STATUS
//
// MessageText:
//
// Failed to update the run custom script status for virtual machine: %1!s!.%0
//
#define ERROR_INSTANT_VM_UPDATE_EXEC_SCRIPT_STATUS ((DWORD)0xC01B0071L)

//
// MessageId: INSTANT_VM_RESTARTING_IVM_JOB
//
// MessageText:
//
// Restarting the %1!s! job. Process ID [%2!s!]. The log file is [%3!s!].%0
//
#define INSTANT_VM_RESTARTING_IVM_JOB    ((DWORD)0x401B0072L)

//
// MessageId: INSTANT_VM_ERROR_FEATURE_NOT_ENABLE
//
// MessageText:
//
// You need to enable the Windows Server Role for Network File System (NFS) on the recovery server: [%1!s!]. Please enable this functionality and retry.%0
//
#define INSTANT_VM_ERROR_FEATURE_NOT_ENABLE ((DWORD)0xC01B0073L)

//
// MessageId: INSTANT_VM_ERROR_FEATURE_NOT_RUNNING
//
// MessageText:
//
// The Windows Server Role for Network File System (NFS) must be running on the recovery server: [%1!s!]. Please start this functionality and retry.??%0
//
#define INSTANT_VM_ERROR_FEATURE_NOT_RUNNING ((DWORD)0xC01B0074L)

//
// MessageId: INSTANT_VM_ERROR_FAIL_LOAD_BINARY
//
// MessageText:
//
// Failed to load binary file [%1!s!]. Error message: %2!s!%0
//
#define INSTANT_VM_ERROR_FAIL_LOAD_BINARY ((DWORD)0xC01B0075L)

//
// MessageId: INSTANT_VM_ERROR_PATH_INVALID
//
// MessageText:
//
// Invalid path: [%1!s!].%0
//
#define INSTANT_VM_ERROR_PATH_INVALID    ((DWORD)0xC01B0076L)

//
// MessageId: INSTANT_VM_ERROR_DRIVE_TYPE_CDROM
//
// MessageText:
//
// The root directory [%1!s!] is a CD-ROM.%0
//
#define INSTANT_VM_ERROR_DRIVE_TYPE_CDROM ((DWORD)0xC01B0077L)

//
// MessageId: INSTANT_VM_ERROR_DRIVE_TYPE_FLASH_DISK
//
// MessageText:
//
// The root directory [%1!s!] is a flash disk.%0
//
#define INSTANT_VM_ERROR_DRIVE_TYPE_FLASH_DISK ((DWORD)0xC01B0078L)

//
// MessageId: INSTANT_VM_AGENT_EXIT
//
// MessageText:
//
// The ^AU_ProductName_IVM_SHORT^ job has completed and the ^AU_ProductName_IVM_SHORT^ Agent has ended.%0
//
#define INSTANT_VM_AGENT_EXIT            ((DWORD)0x401B0079L)

//
// MessageId: ERROR_INSTANT_VM_CHECK_FILE_SYSTEM_FAT32
//
// MessageText:
//
// The volume [%1!s!] file system is %2!s!. As a result, it does not support creation of a file lager than 4GB.%0
//
#define ERROR_INSTANT_VM_CHECK_FILE_SYSTEM_FAT32 ((DWORD)0xC01B007AL)

//
// MessageId: INSTANT_VM_ERROR_DISK_CREATION
//
// MessageText:
//
// Failed to create the dummy virtual hard disk file %1!s!. Error: %2!d!.%0
//
#define INSTANT_VM_ERROR_DISK_CREATION   ((DWORD)0xC01B007BL)

//
// MessageId: INSTANT_VM_ERROR_GET_DISK_FILE_SIZE
//
// MessageText:
//
// Failed to get the file size attribute of the dummy virtual hard disk file %1!s!. Error: %2!d!.%0
//
#define INSTANT_VM_ERROR_GET_DISK_FILE_SIZE ((DWORD)0xC01B007CL)

//
// MessageId: INSTANT_VM_ERROR_GET_DISK_FILE_GUID
//
// MessageText:
//
// Failed to get the file GUID of the dummy virtual hard disk file %1!s!. Error: %2!d!.%0
//
#define INSTANT_VM_ERROR_GET_DISK_FILE_GUID ((DWORD)0xC01B007DL)

//
// MessageId: INSTANT_VM_ERROR_PRERUN_CHECK
//
// MessageText:
//
// Failed to perform the prerun check against the proxy server %1!s!. Please check the folder attribute where the virtual disk will be created.%0
//
#define INSTANT_VM_ERROR_PRERUN_CHECK    ((DWORD)0xC01B007EL)

//
// MessageId: INSTANT_VM_ERROR_CREATE_DIFFERENCING_DISK
//
// MessageText:
//
// Failed to create the differencing virtual disk %1!s! by the parent disk file %2!s!. Error: %3!d!.%0
//
#define INSTANT_VM_ERROR_CREATE_DIFFERENCING_DISK ((DWORD)0xC01B007FL)

//
// MessageId: INSTANT_VM_WARN_NO_INTEGR_SVC
//
// MessageText:
//
// The Hyper-V integration service has not been installed on the source machine.
//
#define INSTANT_VM_WARN_NO_INTEGR_SVC    ((DWORD)0x801B0080L)

//
// MessageId: INSTANT_VM_WARN_DYNAMIC_DISK
//
// MessageText:
//
// Since the system volume or the boot volume is on the dynamic disk, the driver injection operation is canceled out. 
//
#define INSTANT_VM_WARN_DYNAMIC_DISK     ((DWORD)0x801B0081L)

//
// MessageId: INSTANT_VM_WARN_DELETE_FOLDER
//
// MessageText:
//
// Failed to delete the directory %1!s!.%0
//
#define INSTANT_VM_WARN_DELETE_FOLDER    ((DWORD)0x801B0082L)

//
// MessageId: INSTANT_VM_ERROR_ADJUST_DISK_GEOMETRY
//
// MessageText:
//
// Failed to adjust the disk geometry of the disk file %1!s!.
//
#define INSTANT_VM_ERROR_ADJUST_DISK_GEOMETRY ((DWORD)0xC01B0083L)

//
// MessageId: INSTANT_VM_START_ADJUST_SECTOR_SIZE
//
// MessageText:
//
// Start to adjust the disk geometry with the 512 bytes per sector for virtual machine %1!s! to the 4K bytes per sector.%0?
//
#define INSTANT_VM_START_ADJUST_SECTOR_SIZE ((DWORD)0x401B0084L)

//
// MessageId: INSTANT_VM_ADJUST_SECTOR_SIZE_OK
//
// MessageText:
//
// The disks with the disk geometry of the 512 bytes per sector of virtual machine %1!s! have been adjusted to the 4K bytes per sector successfully.%0
//
#define INSTANT_VM_ADJUST_SECTOR_SIZE_OK ((DWORD)0x401B0085L)

//
// MessageId: INSTANT_VM_ADJUST_SECTOR_SIZE_ERROR
//
// MessageText:
//
// Failed to adjust the disks of virtual machine %1!s! to the disk geometry of the 4KB bytes per sector.%0
//
#define INSTANT_VM_ADJUST_SECTOR_SIZE_ERROR ((DWORD)0xC01B0086L)

//
// MessageId: INSTANT_VM_STARTING_NFS_FEATURE
//
// MessageText:
//
// Starting to install the Windows Server Role for Network File System (NFS) on the Recover Server %1!s!.%0
//
#define INSTANT_VM_STARTING_NFS_FEATURE  ((DWORD)0x401B0087L)

//
// MessageId: INSTANT_VM_FINISH_NFS_FEATURE
//
// MessageText:
//
// The Windows Server Role for Network File System (NFS) has been installed successfully.%0
//
#define INSTANT_VM_FINISH_NFS_FEATURE    ((DWORD)0x401B0088L)

//
// MessageId: INSTANT_VM_ERROR_NFS_FEATURE
//
// MessageText:
//
// Failed to install the Windows Server Role for Network File System (NFS). Error: %1!d!. Please setup this role for Network File System manually and try again.%0 
//
#define INSTANT_VM_ERROR_NFS_FEATURE     ((DWORD)0xC01B0089L)

//
// MessageId: INSTANT_VM_ERROR_NFS_FEATURE_REBOOT_REQUIRED
//
// MessageText:
//
// Rebooting is requried before the Windows Server Role for Network File System (NFS) can take effect. Please reboot this machine manually and try agian.%0
//
#define INSTANT_VM_ERROR_NFS_FEATURE_REBOOT_REQUIRED ((DWORD)0xC01B008AL)

//
// MessageId: INSTANT_VM_WARN_CLIENT_VER_SUPPORT
//
// MessageText:
//
// Windows 7 or later Client Hyper-V is neither forbidden nor officially supported by ^AU_ProductName_IVM_SHORT^ on the host machine %1!s!. You are responsible for the test results.%0
//
#define INSTANT_VM_WARN_CLIENT_VER_SUPPORT ((DWORD)0x801B008BL)

//
// MessageId: INSTANT_VM_CREATE_NFS_SHARE_ON_MOUNT_POINT_FOLDER
//
// MessageText:
//
// Unable to create NFS share for the path [%1!s!], which is under a mount point. Select another folder and retry.%0 
//
#define INSTANT_VM_CREATE_NFS_SHARE_ON_MOUNT_POINT_FOLDER ((DWORD)0xC01B008CL)

//
// MessageId: ERROR_INSTANT_VM_CHECK_FILE_SYSTEM
//
// MessageText:
//
// The volume [%1!s!] file system is %2!s!. To create a sparse file, you must select a folder on an NTFS or ReFS volume.%0??
//
#define ERROR_INSTANT_VM_CHECK_FILE_SYSTEM ((DWORD)0xC01B008DL)

//
// MessageId: INSTANT_VM_WARN_HYPERV_VM_TOOL_NOT_EXIST
//
// MessageText:
//
// The integration service ISO file [%1!s!] does not exist. It will not be installed after the instant VM [%2!s!] is started. To install it manually, refer to Microsoft Windows Hyper-V documentation.?%0
//
#define INSTANT_VM_WARN_HYPERV_VM_TOOL_NOT_EXIST ((DWORD)0x801B008EL)

//
// MessageId: INSTANT_VM_WARN_CONVERT_ADAPTER_TYPE
//
// MessageText:
//
// The boot firmware of source machine is EFI. So a generation 2 virtual machine which does not support legacy network adapter will be created on Hyper-V server. As a result the Legacy Network Adapter will be changed to a normal Network Adapter.%0
//
#define INSTANT_VM_WARN_CONVERT_ADAPTER_TYPE ((DWORD)0x801B008FL)

//
// MessageId: INSTANT_VM_JOB_CANCELLED
//
// MessageText:
//
// The %1!s! job has been cancelled.%0
//
#define INSTANT_VM_JOB_CANCELLED         ((DWORD)0x401B0090L)

//
// MessageId: INSTANT_VM_INJECT_FILE_FAIL
//
// MessageText:
//
// Failed to inject %1!s!.%0
//
#define INSTANT_VM_INJECT_FILE_FAIL      ((DWORD)0x801B0091L)

//
// MessageId: INSTANT_VM_CREATE_FOLDER_FAIL
//
// MessageText:
//
// Failed to create directory %1!s!.%0
//
#define INSTANT_VM_CREATE_FOLDER_FAIL    ((DWORD)0x801B0092L)

//
// MessageId: INSTANT_VM_CREATE_NFS_DATASTORE_FAIL
//
// MessageText:
//
// Failed to create NFS DataStore. %1!s!.%0 
//
#define INSTANT_VM_CREATE_NFS_DATASTORE_FAIL ((DWORD)0xC01B0093L)

//
// MessageId: INSTANT_VM_ON_DATA_READ_ERROR
//
// MessageText:
//
// Failed to process the virtual disk file [%1!s!] with the disk GUID [%2!s!]. Error message: Failed to read disk data from the backup destination.%0 
//
#define INSTANT_VM_ON_DATA_READ_ERROR    ((DWORD)0xC01B0094L)

//
// MessageId: INSTANT_VM_ON_DRIVER_LOAD_ERROR
//
// MessageText:
//
// Failed to load the driver file [%1!s!]. Error: %2!d!. Error message: %3!s!%0
//
#define INSTANT_VM_ON_DRIVER_LOAD_ERROR  ((DWORD)0xC01B0095L)

 // The following are the message definitions for Merge
//
// MessageId: AFRES_MERGE_JOB_START
//
// MessageText:
//
// The merge job started. (Process ID=[%1!u!])%0
//
#define AFRES_MERGE_JOB_START            ((DWORD)0x40110001L)

//
// MessageId: AFRES_MERGE_JOB_STOP
//
// MessageText:
//
// The merge job stopped.%0
//
#define AFRES_MERGE_JOB_STOP             ((DWORD)0x80110002L)

//
// MessageId: AFRES_MERGE_JOB_END
//
// MessageText:
//
// The merge job ended.%0
//
#define AFRES_MERGE_JOB_END              ((DWORD)0x40110003L)

//
// MessageId: AFRES_MERGE_JOB_SUCCEED
//
// MessageText:
//
// The merge job completed successfully.%0
//
#define AFRES_MERGE_JOB_SUCCEED          ((DWORD)0x40110004L)

//
// MessageId: AFRES_MERGE_JOB_FAILED
//
// MessageText:
//
// The merge job failed.%0
//
#define AFRES_MERGE_JOB_FAILED           ((DWORD)0xC0110005L)

//
// MessageId: AFRES_MERGE_JOB_SKIP
//
// MessageText:
//
// The merge job skipped. No session needs to be merged.%0
//
#define AFRES_MERGE_JOB_SKIP             ((DWORD)0x80110006L)

//
// MessageId: AFRES_MERGE_JOB_INIT_BKDEST_SUCCEED
//
// MessageText:
//
// Initialize of the backup destination was successful. (Path=[%1!s!])%0
//
#define AFRES_MERGE_JOB_INIT_BKDEST_SUCCEED ((DWORD)0x40110007L)

//
// MessageId: AFRES_MERGE_JOB_INIT_BKDEST_FAILED
//
// MessageText:
//
// Failed to initialize backup destination [%1!s!]. Please verify if your backup destination is reachable or if you have ever changed the credentials of backup destination.%0
//
#define AFRES_MERGE_JOB_INIT_BKDEST_FAILED ((DWORD)0xC0110008L)

//
// MessageId: AFRES_MERGE_JOB_LOCK_SESS_SUCCEED
//
// MessageText:
//
// Lock session for merge succeeded.%0
//
#define AFRES_MERGE_JOB_LOCK_SESS_SUCCEED ((DWORD)0x40110009L)

//
// MessageId: AFRES_MERGE_JOB_LOCK_SESS_FAILED
//
// MessageText:
//
// Lock session for merge failed.%0
//
#define AFRES_MERGE_JOB_LOCK_SESS_FAILED ((DWORD)0x8011000AL)

//
// MessageId: AFRES_MERGE_JOB_CONTINUE_FAILED_MERGE
//
// MessageText:
//
// Continue to merge the session from the last incomplete or failed merge job.%0
//
#define AFRES_MERGE_JOB_CONTINUE_FAILED_MERGE ((DWORD)0x4011000BL)

//
// MessageId: AFRES_MERGE_JOB_SESS_MERGE_INFO
//
// MessageText:
//
// The retention count is %1!u!. Sessions from %2!u! to %3!u! will be merged.%0
//
#define AFRES_MERGE_JOB_SESS_MERGE_INFO  ((DWORD)0x4011000CL)

//
// MessageId: AFRES_MERGE_JOB_MERGE_SESSION_START
//
// MessageText:
//
// Merge session data started.%0
//
#define AFRES_MERGE_JOB_MERGE_SESSION_START ((DWORD)0x4011000DL)

//
// MessageId: AFRES_MERGE_JOB_MERGE_SESSION_END
//
// MessageText:
//
// Merge session data ended.%0
//
#define AFRES_MERGE_JOB_MERGE_SESSION_END ((DWORD)0x4011000EL)

//
// MessageId: AFRES_MERGE_JOB_MERGE_SESSION_STAT
//
// MessageText:
//
// A total of %1!u! disks in %2!u! sessions have been merged.%0
//
#define AFRES_MERGE_JOB_MERGE_SESSION_STAT ((DWORD)0x4011000FL)

//
// MessageId: AFRES_MERGE_JOB_UNINIT_BKDEST_SUCCEED
//
// MessageText:
//
// Un-initialize of the backup destination was successful.%0
//
#define AFRES_MERGE_JOB_UNINIT_BKDEST_SUCCEED ((DWORD)0x40110010L)

//
// MessageId: AFRES_MERGE_JOB_UNINIT_BKDEST_FAILED
//
// MessageText:
//
// Un-initialize of the backup destination failed.%0
//
#define AFRES_MERGE_JOB_UNINIT_BKDEST_FAILED ((DWORD)0xC0110011L)

//
// MessageId: AFRES_MERGE_JOB_UNIQUEDATA_TO_BE_MERGED
//
// MessageText:
//
// A total of %1!s! unique data will be merged.%0
//
#define AFRES_MERGE_JOB_UNIQUEDATA_TO_BE_MERGED ((DWORD)0x40110012L)

//
// MessageId: AFRES_MERGE_JOB_STOP_AT
//
// MessageText:
//
// The merge job stopped at %1!s!.%0
//
#define AFRES_MERGE_JOB_STOP_AT          ((DWORD)0x80110013L)

//
// MessageId: AFRES_MERGE_JOB_FAILED_AT
//
// MessageText:
//
// The merge job failed at %1!s!.%0
//
#define AFRES_MERGE_JOB_FAILED_AT        ((DWORD)0xC0110014L)

//
// MessageId: AFRES_MERGE_JOB_CRASH
//
// MessageText:
//
// The merge job crashed.%0
//
#define AFRES_MERGE_JOB_CRASH            ((DWORD)0xC0110015L)

//
// MessageId: AFRES_MERGE_JOB_SKIP_FAIL_LOCK
//
// MessageText:
//
// The merge job skipped because the session is locked by another operation. Verify if any recovery points are mounted and dismount them.%0
//
#define AFRES_MERGE_JOB_SKIP_FAIL_LOCK   ((DWORD)0x80110016L)

//
// MessageId: AFRES_MERGE_JOB_NO_ENOUGH_FREE_SPACE_4_MERGE
//
// MessageText:
//
// Not enough free space on the backup destination. (Path=[%1!s!], Space Needed=[%2!s!], Free Space=[%3!s!])%0
//
#define AFRES_MERGE_JOB_NO_ENOUGH_FREE_SPACE_4_MERGE ((DWORD)0xC0110017L)

//
// MessageId: AFRES_MERGE_JOB_BKSET_MERGE_INFO
//
// MessageText:
//
// The recovery set count is %1!u!. The recovery set before session %2!u! will be purged.%0
//
#define AFRES_MERGE_JOB_BKSET_MERGE_INFO ((DWORD)0x40110018L)

//
// MessageId: AFRES_MERGE_JOB_MERGE_SPACE_RELEASED
//
// MessageText:
//
// Approximately %1!s! of disk space will become available.%0
//
#define AFRES_MERGE_JOB_MERGE_SPACE_RELEASED ((DWORD)0x40110019L)

//
// MessageId: AFRES_MERGE_JOB_ARCHIVE_LOCK_FAILED
//
// MessageText:
//
// The merge job was skipped because the session is currently locked by the File Copy operation. Verify if any File Copy job failed or was cancelled.%0
//
#define AFRES_MERGE_JOB_ARCHIVE_LOCK_FAILED ((DWORD)0x8011001AL)

//
// MessageId: AFRES_MERGE_JOB_DELET_CATALOG_FAIL
//
// MessageText:
//
// Failed to delete catalog folder. (Folder [%1!s!]) %2!s!%0
//
#define AFRES_MERGE_JOB_DELET_CATALOG_FAIL ((DWORD)0x8011001BL)

//
// MessageId: AFRES_MERGE_JOB_LOCK_SESS_FAILED_BY_MOUNT
//
// MessageText:
//
// Lock session for merge failed. The session is locked by computers: %1!s!.%0
//
#define AFRES_MERGE_JOB_LOCK_SESS_FAILED_BY_MOUNT ((DWORD)0x8011001CL)

//
// MessageId: AFRES_MERGE_JOB_SKIP_NO_ENOUGH_FREE_SPACE
//
// MessageText:
//
// The merge job skipped because there was not enough free space on the backup destination.%0
//
#define AFRES_MERGE_JOB_SKIP_NO_ENOUGH_FREE_SPACE ((DWORD)0x8011001DL)

//
// MessageId: AFRES_MERGE_JOB_OPERATION_NO_ENOUGH_FREE_SPACE
//
// MessageText:
//
// Some operations failed because there was not enough free space on the backup destination.%0
//
#define AFRES_MERGE_JOB_OPERATION_NO_ENOUGH_FREE_SPACE ((DWORD)0xC011001EL)

//
// MessageId: AFRES_MERGE_JOB_FAILED_TO_ENUMERATE_SESSIONS
//
// MessageText:
//
// Failed to get recovery points for merge. Verify that the backup destination folder is available, all recovery points are reachable, and the network connection is working properly.%0
//
#define AFRES_MERGE_JOB_FAILED_TO_ENUMERATE_SESSIONS ((DWORD)0xC011001FL)

//
// MessageId: AFRES_MERGE_JOB_TOTALDATA_TO_BE_MERGED
//
// MessageText:
//
// A total of %1!s! incremental data will be merged.%0
//
#define AFRES_MERGE_JOB_TOTALDATA_TO_BE_MERGED ((DWORD)0x40110020L)

//
// MessageId: AFRES_MERGE_JOB_RELEASE_DISK_SPACE
//
// MessageText:
//
// Total freed disk space on destination is %1!s!.%0
//
#define AFRES_MERGE_JOB_RELEASE_DISK_SPACE ((DWORD)0x40110021L)

//
// MessageId: AFRES_MERGE_JOB_INIT_BKDEST_DS_SUCCEED
//
// MessageText:
//
// Initialize of the backup destination was successful. (RPS [%1!s!], Data Store[%2!s!])%0
//
#define AFRES_MERGE_JOB_INIT_BKDEST_DS_SUCCEED ((DWORD)0x40110022L)

//
// MessageId: AFRES_MERGE_JOB_INIT_BKDEST_DS_FAILED
//
// MessageText:
//
// Failed to initialize backup destination (RPS [%1!s!], Data Store[%2!s!] ). Please verify if your backup destination is reachable or if you have ever changed the credentials of backup destination.%0
//
#define AFRES_MERGE_JOB_INIT_BKDEST_DS_FAILED ((DWORD)0xC0110023L)

//
// MessageId: AFRES_MERGE_JOB_TOTAL_MERGE_RANGE_INFO
//
// MessageText:
//
// %1!u! session range(s) will be merged. (Ranges=[%2!s!])%0
//
#define AFRES_MERGE_JOB_TOTAL_MERGE_RANGE_INFO ((DWORD)0x40110024L)

//
// MessageId: AFRES_MERGE_JOB_CURRENT_MERGE_RANGE_INFO
//
// MessageText:
//
// Session range %1!u! is being merged. (Range=[%2!s!])%0
//
#define AFRES_MERGE_JOB_CURRENT_MERGE_RANGE_INFO ((DWORD)0x40110025L)

//
// MessageId: AFRES_MERGE_JOB_MERGE_RANGE_SUCCEED
//
// MessageText:
//
// Session range %1!u! successfully merged. (Range=[%2!s!])%0
//
#define AFRES_MERGE_JOB_MERGE_RANGE_SUCCEED ((DWORD)0x40110026L)

//
// MessageId: AFRES_MERGE_JOB_MERGE_RANGE_FAILED
//
// MessageText:
//
// Failed to merge session range %1!u!. (Range=[%2!s!]. EC=[0x%3!08x!])%0
//
#define AFRES_MERGE_JOB_MERGE_RANGE_FAILED ((DWORD)0x40110027L)

//
// MessageId: AFRES_MERGE_JOB_MERGE_RANGE_SKIP
//
// MessageText:
//
// Session range %1!u! was skipped because it was already merged in a previous job. (Range=[%2!s!])%0
//
#define AFRES_MERGE_JOB_MERGE_RANGE_SKIP ((DWORD)0x40110028L)

//
// MessageId: AFRES_MERGE_JOB_RESUME_FROM
//
// MessageText:
//
// The previous merge job was incomplete and will resume from %1!s!.%0
//
#define AFRES_MERGE_JOB_RESUME_FROM      ((DWORD)0x40110029L)

//
// MessageId: AFRES_MERGE_JOB_CURRENT_MERGE_RANGE_1_SESS
//
// MessageText:
//
// Session range %1!u! is being merged. (Session Number=[%2!u!])%0
//
#define AFRES_MERGE_JOB_CURRENT_MERGE_RANGE_1_SESS ((DWORD)0x4011002AL)

//
// MessageId: AFRES_MERGE_JOB_MERGE_RANGE_1_SESS_SUCCEED
//
// MessageText:
//
// Session range %1!u! successfully merged. (Session Number=[%2!u!])%0
//
#define AFRES_MERGE_JOB_MERGE_RANGE_1_SESS_SUCCEED ((DWORD)0x4011002BL)

//
// MessageId: AFRES_MERGE_JOB_MERGE_RANGE_1_SESS_FAILED
//
// MessageText:
//
// Failed to merge session range %1!u!. (Session Number=[%2!u!]. EC=[0x%3!08x!])%0
//
#define AFRES_MERGE_JOB_MERGE_RANGE_1_SESS_FAILED ((DWORD)0x4011002CL)

//
// MessageId: AFRES_MERGE_JOB_MERGE_RANGE_1_SESS_SKIP
//
// MessageText:
//
// Session range %1!u! was skipped because it was already merged in a previous job. (Session Number=[%2!u!])%0
//
#define AFRES_MERGE_JOB_MERGE_RANGE_1_SESS_SKIP ((DWORD)0x4011002DL)

//
// MessageId: AFRES_MERGE_JOB_MERGE_SESSION_CANCELED
//
// MessageText:
//
// Merge of session [%1!u!] for the [%2!s!] backup type has been postponed because it has not been archived yet. Merge of the session will be performed after the copy to tape is completed.
//
#define AFRES_MERGE_JOB_MERGE_SESSION_CANCELED ((DWORD)0x8011002EL)

//
// MessageId: AFRES_MERGE_JOB_DEST_UNDER_DELETE
//
// MessageText:
//
// Merge job failed because the backup destination is getting deleted.%0
//
#define AFRES_MERGE_JOB_DEST_UNDER_DELETE ((DWORD)0xC011002FL)

 // The following are the message definitions for MountDriver
//
// MessageId: AFRES_GRT_MNT_REST_001
//
// MessageText:
//
// The restore process failed to mount the Volume. (Volume [%1!s!], Error [%2!d!]).%0
//
#define AFRES_GRT_MNT_REST_001           ((DWORD)0xC0120001L)

//
// MessageId: AFRES_GRT_MNT_REST_002
//
// MessageText:
//
// The restore process failed to unmount the Volume. (Volume [%1!s!], Error [%2!d!]).%0
//
#define AFRES_GRT_MNT_REST_002           ((DWORD)0xC0120002L)

//
// MessageId: AFRES_GRT_MNT_REST_003
//
// MessageText:
//
// Failed to set compression level for the File/Directory. (File/Directory [%1!s!], Error [%2!d!]).%0
//
#define AFRES_GRT_MNT_REST_003           ((DWORD)0xC0120003L)

//
// MessageId: AFRES_GRT_MNT_REST_004
//
// MessageText:
//
// An unexpected exception error occurred in GRTMntBrowser.dll.%0
//
#define AFRES_GRT_MNT_REST_004           ((DWORD)0xC0120004L)

//
// MessageId: AFRES_GRT_MNT_REST_005
//
// MessageText:
//
// Failed to restore the File. (File [%1!s!], Error [%2!s!]).%0
//
#define AFRES_GRT_MNT_REST_005           ((DWORD)0xC0120005L)

//
// MessageId: AFRES_GRT_MNT_REST_006
//
// MessageText:
//
// Skipping restore of the File. (File [%1!s!]).%0
//
#define AFRES_GRT_MNT_REST_006           ((DWORD)0x80120006L)

//
// MessageId: AFRES_GRT_MNT_REST_007
//
// MessageText:
//
// Skipping restore of the Directory. (Directory [%1!s!]).%0
//
#define AFRES_GRT_MNT_REST_007           ((DWORD)0x80120007L)

//
// MessageId: AFRES_GRT_MNT_REST_008
//
// MessageText:
//
// Failed to read from the File. (File [%1!s!], Error [%2!s!]).%0
//
#define AFRES_GRT_MNT_REST_008           ((DWORD)0xC0120008L)

//
// MessageId: AFRES_GRT_MNT_REST_009
//
// MessageText:
//
// Failed to write to the File. (File [%1!s!], Error [%2!s!]).%0
//
#define AFRES_GRT_MNT_REST_009           ((DWORD)0xC0120009L)

//
// MessageId: AFRES_GRT_MNT_REST_010
//
// MessageText:
//
// Failed to open the File. (File [%1!s!], Error [%2!s!]).%0
//
#define AFRES_GRT_MNT_REST_010           ((DWORD)0xC012000AL)

//
// MessageId: AFRES_GRT_MNT_REST_011
//
// MessageText:
//
// Failed to create the File. (File [%1!s!], Error [%2!d!]).%0
//
#define AFRES_GRT_MNT_REST_011           ((DWORD)0xC012000BL)

//
// MessageId: AFRES_GRT_MNT_REST_012
//
// MessageText:
//
// Failed to set sparse attribute for the File/Directory. (File/Directory [%1!s!], Error [%2!d!]).%0
//
#define AFRES_GRT_MNT_REST_012           ((DWORD)0xC012000CL)

//
// MessageId: AFRES_GRT_MNT_REST_013
//
// MessageText:
//
// Perform an optimized restore. Source volume %1!s!, Destination volume %2!s!.%0
//
#define AFRES_GRT_MNT_REST_013           ((DWORD)0x4012000DL)

//
// MessageId: AFRES_GRT_MNT_REST_014
//
// MessageText:
//
// The destination volume is not empty. Perform an unoptimized restore. Source volume %1!s!, Destination volume %2!s!.%0
//
#define AFRES_GRT_MNT_REST_014           ((DWORD)0x4012000EL)

//
// MessageId: AFRES_GRT_MNT_REST_015
//
// MessageText:
//
// Perform an unoptimized restore. Source volume %1!s!, Destination volume %2!s!.%0
//
#define AFRES_GRT_MNT_REST_015           ((DWORD)0x4012000FL)

//
// MessageId: AFRES_GRT_MNT_REST_016
//
// MessageText:
//
// Perform an optimized restore. After the restore job completes, install the Data Deduplication Feature before you access the restored data. Source volume %1!s!, Destination volume %2!s!.%0
//
#define AFRES_GRT_MNT_REST_016           ((DWORD)0x80120010L)

//
// MessageId: AFRES_GRT_MNT_REST_017
//
// MessageText:
//
// This is not supported. The Data Deduplication Feature is not installed or the destination volume is not empty. Source volume %1!s!, Destination volume %2!s!.%0
//
#define AFRES_GRT_MNT_REST_017           ((DWORD)0xC0120011L)

//
// MessageId: AFRES_GRT_MNT_REST_018
//
// MessageText:
//
// This is not supported. The Data Deduplication Feature is not installed. Source volume %1!s!, Destination volume %2!s!.%0
//
#define AFRES_GRT_MNT_REST_018           ((DWORD)0xC0120012L)

//
// MessageId: AFRES_GRT_MNT_REST_019
//
// MessageText:
//
// ^AU_ProductName_UDP_SHORT^ does not support a restore of data that was backed up from a deduplication-enabled volume %1!s! on a Server 2012 or later to a target volume %2!s! on a system that is running an operating system earlier than Server 2012.%0
//
#define AFRES_GRT_MNT_REST_019           ((DWORD)0xC0120013L)

//
// MessageId: AFRES_GRT_MNT_REST_SKIP_ENCRYPTEDFILE
//
// MessageText:
//
// Skipping restore encrypted file[%1!s!] into Non-NTFS volume.%0
//
#define AFRES_GRT_MNT_REST_SKIP_ENCRYPTEDFILE ((DWORD)0x80120014L)

//
// MessageId: AFRES_MNT_SKIP_ADSFILE_INTO_REFS
//
// MessageText:
//
// Skipping restore Alternate Data Stream file[%1!s!] into Non-NTFS volume.%0
//
#define AFRES_MNT_SKIP_ADSFILE_INTO_REFS ((DWORD)0x40120015L)

//
// MessageId: AFRES_MNT_LOSE_COMPRESS_ATTr_INTO_Refs
//
// MessageText:
//
// As the system limitation, file [%1!s!] will lose Compress attribute after restored into ReFS Volume.%0
//
#define AFRES_MNT_LOSE_COMPRESS_ATTr_INTO_Refs ((DWORD)0x80120016L)

//
// MessageId: AFRES_MNT_LOSE_INTEGRITY_ATTr_INTO_Ntfs
//
// MessageText:
//
// As the system limitation, file [%1!s!] will lose Integrity attribute after restored into NTFS Volume.%0
//
#define AFRES_MNT_LOSE_INTEGRITY_ATTr_INTO_Ntfs ((DWORD)0x80120017L)

//
// MessageId: AFRES_MNT_SPARSE_CANT_RESTORE2_NONRefs
//
// MessageText:
//
// As the system limitation, file [%1!s!] can not be restored into volume that doesn't support sparse file.%0
//
#define AFRES_MNT_SPARSE_CANT_RESTORE2_NONRefs ((DWORD)0xC0120018L)

//
// MessageId: AFRES_MNT_SKIP_ADS_FILESTREAM_INTO_NON_NTFS
//
// MessageText:
//
// Skipping restore of named file streams of Alternate Data Stream file[%1!s!] into Non-NTFS volume.%0
//
#define AFRES_MNT_SKIP_ADS_FILESTREAM_INTO_NON_NTFS ((DWORD)0x80120019L)

//
// MessageId: AFRES_MNT_SKIP_REST_HARD_LINK_INTO_XP
//
// MessageText:
//
// Skip recovery of hard link: [%1!s!]. For information about how to recover a hard link, see the product documentation.%0
//
#define AFRES_MNT_SKIP_REST_HARD_LINK_INTO_XP ((DWORD)0x8012001AL)

//
// MessageId: AFRES_MNT_SKIP_EA_DATA_INTO_NON_NTFS
//
// MessageText:
//
// Skipping restore of extended attribute data of [%1!s!] into a non-NTFS volume.%0
//
#define AFRES_MNT_SKIP_EA_DATA_INTO_NON_NTFS ((DWORD)0x8012001BL)

//
// MessageId: AFRES_GRT_MNT_REST_LOCKSESS
//
// MessageText:
//
// The restore process failed to mount Volume [%1!s!] because the session [%2!d!] failed to lock. Please verify that there are no other jobs running on this session.%0
//
#define AFRES_GRT_MNT_REST_LOCKSESS      ((DWORD)0xC012001CL)

 // The following are the message definitions for Purge
//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_START
//
// MessageText:
//
// Starting node data purge on ^AU_ProductName_SERVER^ [%1!s!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_START   ((DWORD)0x401C0001L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_DATASTORE
//
// MessageText:
//
// Purging nodes from data store [%1!s!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_DATASTORE ((DWORD)0x401C0002L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_ADVANCE_FORMAT
//
// MessageText:
//
// The Backup Data Format for the data store is configured in the Advanced format%0
//
#define AFRES_RPS_NODE_PURGE_JOB_ADVANCE_FORMAT ((DWORD)0x401C0003L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_DEDUPE_FORMAT
//
// MessageText:
//
// The Backup Data Format for the data store is configured in the Advanced format with Data Deduplication enabled%0
//
#define AFRES_RPS_NODE_PURGE_JOB_DEDUPE_FORMAT ((DWORD)0x401C0004L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_SUCCESS
//
// MessageText:
//
// Node data purge job completed successfully%0
//
#define AFRES_RPS_NODE_PURGE_JOB_SUCCESS ((DWORD)0x401C0005L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FAILED
//
// MessageText:
//
// Node data purge job failed%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FAILED  ((DWORD)0xC01C0006L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_TOTAL_NODES_NUM
//
// MessageText:
//
// Total number of agent nodes to be purged: [%1!d!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_TOTAL_NODES_NUM ((DWORD)0x401C0007L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FAILED_NODES
//
// MessageText:
//
// Failed to purge agent node: [%1!s!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FAILED_NODES ((DWORD)0xC01C0008L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FAILED_NODES_NUM
//
// MessageText:
//
// Total number of agent nodes that failed to purge: [%1!d!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FAILED_NODES_NUM ((DWORD)0x801C0009L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FINISH_NODES_NUM
//
// MessageText:
//
// Total number of agent nodes that were successfully purged: [%1!d!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FINISH_NODES_NUM ((DWORD)0x401C000AL)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_DELETE_FILE_FAILED
//
// MessageText:
//
// Failed to delete file: [%1!s!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_DELETE_FILE_FAILED ((DWORD)0xC01C000BL)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_PURGE_DEDUP_SESS_FAILED
//
// MessageText:
//
// Failed to purge data from the data store for node [%1!s!], session number [%2!d!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_PURGE_DEDUP_SESS_FAILED ((DWORD)0xC01C000CL)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_NODE_NAME_SUCCESS
//
// MessageText:
//
// Successfully purged agent node: [%1!s!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_NODE_NAME_SUCCESS ((DWORD)0x401C000DL)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_VM_NODE_NAME_SUCCESS
//
// MessageText:
//
// Successfully purged Host-Based VM node: [%1!s!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_VM_NODE_NAME_SUCCESS ((DWORD)0x401C000EL)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_VM_NODE_NAME_FAILUE
//
// MessageText:
//
// Failed to purge Host-Based VM node: [%1!s!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_VM_NODE_NAME_FAILUE ((DWORD)0xC01C000FL)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_CRASH
//
// MessageText:
//
// Node data purge job crashed%0
//
#define AFRES_RPS_NODE_PURGE_JOB_CRASH   ((DWORD)0xC01C0010L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FAILED_OTHER_JOB_RUNNING
//
// MessageText:
//
// Unable to delete agent node:[%1!s!]because it is currently in use or its backup destination path is not accessible%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FAILED_OTHER_JOB_RUNNING ((DWORD)0xC01C0011L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FAILED_VM_OTHER_JOB_RUNNING
//
// MessageText:
//
// Unable to delete Host-Based VM node:[%1!s!] because it is currently in use or its backup destination path is not accessible%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FAILED_VM_OTHER_JOB_RUNNING ((DWORD)0xC01C0012L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FINISH_VM_NODES_NUM
//
// MessageText:
//
// Total number of Host-Based VM nodes that were successfully purged: [%1!d!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FINISH_VM_NODES_NUM ((DWORD)0x401C0013L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_TOTAL_VM_NODES_NUM
//
// MessageText:
//
// Total number of Host-Based VM nodes to be purged: [%1!d!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_TOTAL_VM_NODES_NUM ((DWORD)0x401C0014L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_FAILED_VM_NODES_NUM
//
// MessageText:
//
// Total number of Host-Based VM nodes that failed to purge: [%1!d!]%0
//
#define AFRES_RPS_NODE_PURGE_JOB_FAILED_VM_NODES_NUM ((DWORD)0xC01C0015L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_NOT_FIND_NODES
//
// MessageText:
//
// The agent node [%1!s!] does not exist%0
//
#define AFRES_RPS_NODE_PURGE_JOB_NOT_FIND_NODES ((DWORD)0xC01C0016L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_NOT_FIND_VM_NODES
//
// MessageText:
//
// The Host-Based VM node [%1!s!] does not exist%0
//
#define AFRES_RPS_NODE_PURGE_JOB_NOT_FIND_VM_NODES ((DWORD)0xC01C0017L)

 // The following are the message definitions for Restore
//
// MessageId: AFRES_AFREST_JOB_SUCCESS
//
// MessageText:
//
// The restore job completed successfully.%0
//
#define AFRES_AFREST_JOB_SUCCESS         ((DWORD)0x40130001L)

//
// MessageId: AFRES_AFREST_JOB_FAILED
//
// MessageText:
//
// Restore job failed.%0
//
#define AFRES_AFREST_JOB_FAILED          ((DWORD)0xC0130002L)

//
// MessageId: AFRES_AFREST_VSS_ALTERLOCATION
//
// MessageText:
//
// Writer does not allow restore to alternative location.%0
//
#define AFRES_AFREST_VSS_ALTERLOCATION   ((DWORD)0xC0130003L)

//
// MessageId: AFRES_AFREST_JOB_CANCELED
//
// MessageText:
//
// Restore job was canceled.%0
//
#define AFRES_AFREST_JOB_CANCELED        ((DWORD)0x80130004L)

//
// MessageId: AFRES_AFREST_FAIL_LOCATE_SOURCE
//
// MessageText:
//
// Failed to locate restore source. (Path=[%1!s!], Username=[%2!s!], EC=[%3!d!]).%0
//
#define AFRES_AFREST_FAIL_LOCATE_SOURCE  ((DWORD)0xC0130005L)

//
// MessageId: AFRES_AFREST_START_RESTORE
//
// MessageText:
//
// [PID: %1!d!] Start Restore Operation.%0
//
#define AFRES_AFREST_START_RESTORE       ((DWORD)0x40130006L)

//
// MessageId: AFRES_AFREST_RESTORE_SOURCE
//
// MessageText:
//
// Restore files from %1!s!.%0
//
#define AFRES_AFREST_RESTORE_SOURCE      ((DWORD)0x40130007L)

//
// MessageId: AFRES_AFREST_RESTORE_DESTINATION_ORIG
//
// MessageText:
//
// Restore to original location.%0
//
#define AFRES_AFREST_RESTORE_DESTINATION_ORIG ((DWORD)0x40130008L)

//
// MessageId: AFRES_AFREST_RESTORE_DESTINATION_ALTER
//
// MessageText:
//
// Restore to alternate location. (Destination Path=[%1!s!])%0
//
#define AFRES_AFREST_RESTORE_DESTINATION_ALTER ((DWORD)0x40130009L)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS
//
// MessageText:
//
// Restore options:%0
//
#define AFRES_AFREST_RESTORE_OPTIONS     ((DWORD)0x4013000AL)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_YES
//
// MessageText:
//
// Yes%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_YES ((DWORD)0x4013000BL)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_NO
//
// MessageText:
//
// No%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_NO  ((DWORD)0x4013000CL)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_OVERWRITEFILE
//
// MessageText:
//
// Conflict resolution: overwrite existing files.%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_OVERWRITEFILE ((DWORD)0x4013000DL)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_ACTIVEFILE
//
// MessageText:
//
// Conflict resolution: overwrite existing files, replace active files.%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_ACTIVEFILE ((DWORD)0x4013000EL)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_ROOTDIRECTORY
//
// MessageText:
//
// Create root directory: %1!s!.%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_ROOTDIRECTORY ((DWORD)0x4013000FL)

//
// MessageId: AFRES_AFREST_RESTORE_FS_SUMMARY
//
// MessageText:
//
// %1!d! directories %2!d! files(%3!s!) restore to disk, elapsed time %4!s!, restore job throughput %5!s!/Min.%0
//
#define AFRES_AFREST_RESTORE_FS_SUMMARY  ((DWORD)0x40130010L)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_APPSQL_DUMPFILEONLY
//
// MessageText:
//
// Dump file only. (Destination Path=[%1!s!])%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_APPSQL_DUMPFILEONLY ((DWORD)0x40130011L)

//
// MessageId: AFRES_AFREST_RESTORE_DESTINATION_ALTER_APPSQL
//
// MessageText:
//
// Restore to alternative location.%0
//
#define AFRES_AFREST_RESTORE_DESTINATION_ALTER_APPSQL ((DWORD)0x40130012L)

//
// MessageId: AFRES_AFREST_RESTORE_DESTINATION_ALTER_APPSQL_ITEM
//
// MessageText:
//
// Restore %1!s! as %2!s! at %3!s!.%0
//
#define AFRES_AFREST_RESTORE_DESTINATION_ALTER_APPSQL_ITEM ((DWORD)0x40130013L)

//
// MessageId: AFRES_AFREST_DELAY_RESTORE
//
// MessageText:
//
// Cannot do restore now because merging session is in progress.%0
//
#define AFRES_AFREST_DELAY_RESTORE       ((DWORD)0x80130014L)

//
// MessageId: AFRES_AFREST_RESTORE_OPEN_SESSION_FAILED
//
// MessageText:
//
// Failed to open session. (EC=[%1!s!], Session Number=[%2!u!], Session Folder=[%3!s!]).%0
//
#define AFRES_AFREST_RESTORE_OPEN_SESSION_FAILED ((DWORD)0xC0130015L)

//
// MessageId: AFRES_AFREST_RESTORE_UNABLE_TO_FIND_SUBSESS
//
// MessageText:
//
// Failed to restore file because unable to find sub-session for this file. (File Path=[%1!s!])%0
//
#define AFRES_AFREST_RESTORE_UNABLE_TO_FIND_SUBSESS ((DWORD)0xC0130016L)

//
// MessageId: AFRES_AFREST_JOB_INCOMPLETED
//
// MessageText:
//
// Restore job is incomplete.%0
//
#define AFRES_AFREST_JOB_INCOMPLETED     ((DWORD)0x80130017L)

//
// MessageId: AFRES_AFREST_SKIP_RESTORIG_UNMOUNTVOL
//
// MessageText:
//
// Restoring files from an unlabeled and un-mounted volume to the original location is not supported. Skipping restore files from such volume. (Volume=[%1!s!])%0
//
#define AFRES_AFREST_SKIP_RESTORIG_UNMOUNTVOL ((DWORD)0x80130018L)

//
// MessageId: AFRES_AFREST_FAIL_TO_MERGE_SESSIONS
//
// MessageText:
//
// Merge Sessions Failed.%0
//
#define AFRES_AFREST_FAIL_TO_MERGE_SESSIONS ((DWORD)0xC0130019L)

//
// MessageId: AFRES_AFREST_FAIL_LOCATE_DEST
//
// MessageText:
//
// Failed to locate restore destination. (Path=[%1!s!], Username=[%2!s!], EC=[%3!d!])%0
//
#define AFRES_AFREST_FAIL_LOCATE_DEST    ((DWORD)0xC013001AL)

//
// MessageId: AFRES_AFREST_MAP_DEST
//
// MessageText:
//
// Mapped %1!s! to %2!s! internally.%0
//
#define AFRES_AFREST_MAP_DEST            ((DWORD)0x4013001BL)

//
// MessageId: AFRES_AFREST_UNMAP_DEST
//
// MessageText:
//
// Unmapped %1!s! from %2!s! internally.%0
//
#define AFRES_AFREST_UNMAP_DEST          ((DWORD)0x4013001CL)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_RENAMEEXISTFILE
//
// MessageText:
//
// Conflict resolution: rename files.%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_RENAMEEXISTFILE ((DWORD)0x4013001DL)

//
// MessageId: AFRES_AFREST_RESTORE_SKIP
//
// MessageText:
//
// %1!d! Directories %2!d! Files Skipped.%0
//
#define AFRES_AFREST_RESTORE_SKIP        ((DWORD)0x4013001EL)

//
// MessageId: AFRES_AFREST_RESTORE_OPTIONS_SKIPEXISTING
//
// MessageText:
//
// Conflict resolution: skip existing files.%0
//
#define AFRES_AFREST_RESTORE_OPTIONS_SKIPEXISTING ((DWORD)0x4013001FL)

//
// MessageId: AFRES_AFREST_NO_DRIVE_LETTER
//
// MessageText:
//
// No more drive letters are available to mount the remote destination %1!s!.%0
//
#define AFRES_AFREST_NO_DRIVE_LETTER     ((DWORD)0xC0130020L)

//
// MessageId: AFRES_AFREST_LOCK_FAILED
//
// MessageText:
//
// Unable to lock session %1!u! for restore. It is possible session has been merged or removed.%0
//
#define AFRES_AFREST_LOCK_FAILED         ((DWORD)0xC0130021L)

//
// MessageId: AFRES_AFREST_RESTORE_SKIP_REFERLOG
//
// MessageText:
//
// Files or directories were skipped. See %1!s! for the files or directories that were skipped.%0
//
#define AFRES_AFREST_RESTORE_SKIP_REFERLOG ((DWORD)0x80130022L)

//
// MessageId: AFRES_AFREST_RESTORE_SOURCE_DATASTOR
//
// MessageText:
//
// Restore files from ^AU_ProductName_SERVER^ [%1!s!], data store [%2!s!], Session [%3!d!].%0
//
#define AFRES_AFREST_RESTORE_SOURCE_DATASTOR ((DWORD)0x40130023L)

//
// MessageId: AFRES_AFREST_RESTORE_CATALOG_LESS
//
// MessageText:
//
// The file system catalog was not created for this recovery point. As a result, this recovery point will be mounted as a volume for recovery.%0
//
#define AFRES_AFREST_RESTORE_CATALOG_LESS ((DWORD)0x40130024L)

//
// MessageId: AFRES_AFREST_VMR_OVERWRITE_OPTION
//
// MessageText:
//
// The option, Overwrite existing virtual machine, is enabled.%0
//
#define AFRES_AFREST_VMR_OVERWRITE_OPTION ((DWORD)0x40130025L)

//
// MessageId: AFRES_AFREST_VMR_BASE
//
// MessageText:
//
// The option, Overwrite existing virtual machine, is enabled.%0
//
#define AFRES_AFREST_VMR_BASE            ((DWORD)0x40130026L)

//
// MessageId: AFRES_AFREST_VMR_POWERON_OPTION
//
// MessageText:
//
// The option, Power on the recovered virtual machine, is enabled.%0
//
#define AFRES_AFREST_VMR_POWERON_OPTION  ((DWORD)0x40130027L)

//
// MessageId: AFRES_AFREST_VMR_TO_ORIGINAL_OPTION
//
// MessageText:
//
// The option, Recover virtual machine to its original location, is enabled.%0
//
#define AFRES_AFREST_VMR_TO_ORIGINAL_OPTION ((DWORD)0x40130028L)

//
// MessageId: AFRES_AFREST_VMR_TO_DEST_ESX
//
// MessageText:
//
// The destination server is %1!s!.%0
//
#define AFRES_AFREST_VMR_TO_DEST_ESX     ((DWORD)0x40130029L)

//
// MessageId: AFRES_AFREST_VMR_TO_DEST_VM
//
// MessageText:
//
// The destination virtual machine name is %1!s!.%0
//
#define AFRES_AFREST_VMR_TO_DEST_VM      ((DWORD)0x4013002AL)

//
// MessageId: AFRES_AFREST_VMR_SOURCE
//
// MessageText:
//
// The source session path is %1!s! and session number is %2!u!.%0
//
#define AFRES_AFREST_VMR_SOURCE          ((DWORD)0x4013002BL)

//
// MessageId: AFRES_AFREST_VMR_INIT_SOURCE
//
// MessageText:
//
// Source session initializing.%0
//
#define AFRES_AFREST_VMR_INIT_SOURCE     ((DWORD)0x4013002CL)

//
// MessageId: AFRES_AFREST_VMR_INIT_VM_LIB
//
// MessageText:
//
// Initializing virtual machine manager libraries.%0
//
#define AFRES_AFREST_VMR_INIT_VM_LIB     ((DWORD)0x4013002DL)

//
// MessageId: AFRES_AFREST_VMR_FOUND_DUP_VM
//
// MessageText:
//
// Virtual Machine %1!s! already exists at ESX server %2!s!.%0
//
#define AFRES_AFREST_VMR_FOUND_DUP_VM    ((DWORD)0x4013002EL)

//
// MessageId: AFRES_AFREST_VMR_RENAME_VM
//
// MessageText:
//
// The virtual machine was renamed to %1!s! from %2!s!.%0
//
#define AFRES_AFREST_VMR_RENAME_VM       ((DWORD)0xC013002FL)

//
// MessageId: AFRES_AFREST_VMR_EXIT_WITHOUT_OWR
//
// MessageText:
//
// Virtual machine %1!s! already exists at ESX server %2!s!. To recover this VM, you must enable the Overwrite option.%0
//
#define AFRES_AFREST_VMR_EXIT_WITHOUT_OWR ((DWORD)0xC0130030L)

//
// MessageId: AFRES_AFREST_VMR_CREATE_VM
//
// MessageText:
//
// Create new virtual machine. (Name=[%1!s!])%0
//
#define AFRES_AFREST_VMR_CREATE_VM       ((DWORD)0x40130031L)

//
// MessageId: AFRES_AFREST_VMR_REP_VMDK
//
// MessageText:
//
// Recovering virtual disks.%0
//
#define AFRES_AFREST_VMR_REP_VMDK        ((DWORD)0x40130032L)

//
// MessageId: AFRES_AFREST_VMR_CREATE_VM_FAIL
//
// MessageText:
//
// VM recovery job was unable to create the new virtual machine.%0
//
#define AFRES_AFREST_VMR_CREATE_VM_FAIL  ((DWORD)0xC0130033L)

//
// MessageId: AFRES_AFREST_VMR_REP_VMDK_FAIL
//
// MessageText:
//
// Failed to recover virtual disks.%0
//
#define AFRES_AFREST_VMR_REP_VMDK_FAIL   ((DWORD)0xC0130034L)

//
// MessageId: AFRES_AFREST_VMR_REP_VMDK_FINISH
//
// MessageText:
//
// Finished recovering virtual disks.%0
//
#define AFRES_AFREST_VMR_REP_VMDK_FINISH ((DWORD)0x40130035L)

//
// MessageId: AFRES_AFREST_VMR_DEL_OLD_VM
//
// MessageText:
//
// Recovery succeeded. Delete the original virtual machine %1!s!(%2!s!).%0
//
#define AFRES_AFREST_VMR_DEL_OLD_VM      ((DWORD)0x40130036L)

//
// MessageId: AFRES_AFREST_VMR_POWER_ON_VM
//
// MessageText:
//
// Powering on recovered virtual machine.%0
//
#define AFRES_AFREST_VMR_POWER_ON_VM     ((DWORD)0x40130037L)

//
// MessageId: AFRES_AFREST_VMR_DEL_FAILED_VM
//
// MessageText:
//
// Delete the unsuccessfully recovered virtual machine.%0
//
#define AFRES_AFREST_VMR_DEL_FAILED_VM   ((DWORD)0x40130038L)

//
// MessageId: AFRES_AFREST_VMR_RN_BACK
//
// MessageText:
//
// The virtual machine recovery did not complete successfully. The virtual machine was renamed back to its original name (from %1!s! to %2!s!).%0
//
#define AFRES_AFREST_VMR_RN_BACK         ((DWORD)0x40130039L)

//
// MessageId: AFRES_AFREST_VMR_IS_VM_NAME_USED
//
// MessageText:
//
// A virtual machine named %1!s! already exists on the target server %2!s!.%0
//
#define AFRES_AFREST_VMR_IS_VM_NAME_USED ((DWORD)0xC013003AL)

//
// MessageId: AFRES_AFREST_VMR_DEST_VCENTER
//
// MessageText:
//
// Virtual Machine will be restored to %1!s!%0
//
#define AFRES_AFREST_VMR_DEST_VCENTER    ((DWORD)0x4013003BL)

//
// MessageId: AFRES_AFREST_VMR_PROTOCOL
//
// MessageText:
//
// The protocol used is %1!s! and port number used is %2!d!.%0
//
#define AFRES_AFREST_VMR_PROTOCOL        ((DWORD)0x4013003CL)

//
// MessageId: AFRES_AFREST_TOTAL_THROUGHPUT_VM
//
// MessageText:
//
// The total size of the recovered data %1!s!, the elapsed time is %2!s!, and the average throughput is %3!s!/Min.%0
//
#define AFRES_AFREST_TOTAL_THROUGHPUT_VM ((DWORD)0x4013003DL)

//
// MessageId: AFRES_AFREST_VMR_POWER_ON_VM_OK
//
// MessageText:
//
// The virtual machine was powered on successfully.%0
//
#define AFRES_AFREST_VMR_POWER_ON_VM_OK  ((DWORD)0x4013003EL)

//
// MessageId: AFRES_AFREST_VMR_POWER_ON_VM_FAIL
//
// MessageText:
//
// Unable to power on recovered virtual machine. The VM may already be powered on.%0
//
#define AFRES_AFREST_VMR_POWER_ON_VM_FAIL ((DWORD)0x8013003FL)

//
// MessageId: AFRES_AFREST_VMR_SKIP_D2D
//
// MessageText:
//
// %1!s! is missing from the copied backup destination and will be skipped during VM recovery. If you wish to include this file in the recovery process, perform Recover VM from the original backup destination.%0
//
#define AFRES_AFREST_VMR_SKIP_D2D        ((DWORD)0x80130040L)

//
// MessageId: AFRES_AFREST_VMR_VMDK_TRANS_MODE
//
// MessageText:
//
// Virtual disk #%1!u! recovery finished using transport mode %2!s!.%0
//
#define AFRES_AFREST_VMR_VMDK_TRANS_MODE ((DWORD)0x40130041L)

//
// MessageId: AFRES_AFREST_VMR_CREATE_VM_FAIL_ERROR
//
// MessageText:
//
// VM recovery job was unable to create the new virtual machine. The ESX/vCenter Server system reported the following error: %1!s!%0
//
#define AFRES_AFREST_VMR_CREATE_VM_FAIL_ERROR ((DWORD)0xC0130042L)

//
// MessageId: AFRES_AFREST_VMR_CREATE_VM_FAIL_UNK_ERROR
//
// MessageText:
//
// VM recovery job was unable to create the new virtual machine. The ESX/vCenter Server system reported the following error: An unknown error occurred.%0
//
#define AFRES_AFREST_VMR_CREATE_VM_FAIL_UNK_ERROR ((DWORD)0xC0130043L)

//
// MessageId: AFRES_AFREST_VMR_VDS
//
// MessageText:
//
// Virtual Machine was configured with Virtual Distributed Switch during backup. The recover VM operation will skip the network configuration associated with Virtual Distributed Switch. Please add the network configuration of Virtual Distributed Switch manually after the VM recovery is completed.%0
//
#define AFRES_AFREST_VMR_VDS             ((DWORD)0x40130044L)

//
// MessageId: AFRES_AFREST_VMDR_START_RESTORE
//
// MessageText:
//
// VM direct restore job will be started,target machine:%1!s!,host machine:%2!s!.%0
//
#define AFRES_AFREST_VMDR_START_RESTORE  ((DWORD)0x40130045L)

//
// MessageId: AFRES_AFREST_VMDR_RESTORE_FAILED
//
// MessageText:
//
// VM direct restore job failed, target machine:%1!s!,host machine:%2!s!.%0
//
#define AFRES_AFREST_VMDR_RESTORE_FAILED ((DWORD)0x40130046L)

//
// MessageId: AFRES_AFREST_VMDR_RESTORE_SUCCESSFULLY
//
// MessageText:
//
// VM direct restore job succeed on machine:%1!s!,host machine:%2!s!.%0
//
#define AFRES_AFREST_VMDR_RESTORE_SUCCESSFULLY ((DWORD)0x40130047L)

//
// MessageId: AFRES_AFREST_VMDR_CONNECT_HOST_FAILED
//
// MessageText:
//
// VM direct restore failed to connect VM host:%1!s!.%0
//
#define AFRES_AFREST_VMDR_CONNECT_HOST_FAILED ((DWORD)0xC0130048L)

//
// MessageId: AFRES_AFREST_VMDR_CONNECT_VM_FAILED
//
// MessageText:
//
// VM direct restore failed to connect VM:%1!s!.%0
//
#define AFRES_AFREST_VMDR_CONNECT_VM_FAILED ((DWORD)0xC0130049L)

//
// MessageId: AFRES_AFREST_VMDR_VM_POWEROFF_OUTOFTOOL
//
// MessageText:
//
// VM direct restore failed, the target VM:%1!s! is power off or VM tools out of date on VM host %2!s!.%0
//
#define AFRES_AFREST_VMDR_VM_POWEROFF_OUTOFTOOL ((DWORD)0x4013004AL)

//
// MessageId: AFRES_AFREST_VMDR_JOB_CANCELED
//
// MessageText:
//
// VM direct restore job is canceled, target machine:%1!s!,host machine:%2!s!.%0
//
#define AFRES_AFREST_VMDR_JOB_CANCELED   ((DWORD)0x4013004BL)

//
// MessageId: AFRES_AFREST_VMR_VDDKEnforceNBD
//
// MessageText:
//
// The VM recovery operation is enforced to use NBD transport mode for VDDK IO.%0
//
#define AFRES_AFREST_VMR_VDDKEnforceNBD  ((DWORD)0x4013004CL)

//
// MessageId: AFRES_AFREST_VMR_DEL_VM_FAILED
//
// MessageText:
//
// Unable to delete the virtual machine %1!s!.%0
//
#define AFRES_AFREST_VMR_DEL_VM_FAILED   ((DWORD)0xC013004DL)

//
// MessageId: AFRES_AFREST_VMR_NETWORK_IS_NOT_AVAILABLE
//
// MessageText:
//
// Some network adapters are not assigned to any of the available networks. The network adapters will be restored as part of the VM recovery, but not connected.%0
//
#define AFRES_AFREST_VMR_NETWORK_IS_NOT_AVAILABLE ((DWORD)0xC013004EL)

//
// MessageId: AFRES_AFREST_VM_END_ACCESS_FAILURE
//
// MessageText:
//
// Failed to notify the host that a backup job is going to start. The ESX/vCenter Server system reported the following error: %1!s!%0
//
#define AFRES_AFREST_VM_END_ACCESS_FAILURE ((DWORD)0xC013004FL)

//
// MessageId: AFRES_AFREST_VFLASH_RESOURCE_NOT_SUFFICIENT
//
// MessageText:
//
// The Virtual Flash Resource (%1!s!) on the target ESX host (%2!s!) is not sufficient for the virtual disks' Virtual Flash Read Cache (%3!s!) . The VM (%4!s!) cannot be powered on. Change the Virtual Flash Read Cache settings after the recovery job is done.%0
//
#define AFRES_AFREST_VFLASH_RESOURCE_NOT_SUFFICIENT ((DWORD)0xC0130050L)

//
// MessageId: AFRES_AFREST_VFLASH_CACHE_CLEARED
//
// MessageText:
//
// There is no Virtual Flash Resource on the target ESX host (%1!s!). As a result, the Virtual Flash Read Cache of the VM (%2!s!)'s virtual disks will be set to 0.%0
//
#define AFRES_AFREST_VFLASH_CACHE_CLEARED ((DWORD)0x80130051L)

//
// MessageId: AFRES_AFREST_RESTORE_VM_SOURCE_DATASTOR
//
// MessageText:
//
// Restore virtual machine from ^AU_ProductName_SERVER^ [%1!s!], data store [%2!s!], Session [%3!d!].%0
//
#define AFRES_AFREST_RESTORE_VM_SOURCE_DATASTOR ((DWORD)0x40130052L)

//
// MessageId: AFRES_AFREST_VMR_RENAME_VM_FAILURE
//
// MessageText:
//
// Failed to rename virtual machine %1!s!.%0
//
#define AFRES_AFREST_VMR_RENAME_VM_FAILURE ((DWORD)0xC0130053L)

//
// MessageId: AFRES_AFREST_RESTORE_EXCH_GRT_SUMMARY
//
// MessageText:
//
// %1!d! folder(s) %2!d! email(s)(%3!s!) from %4!d! mailbox(es) restored to Exchange server, elapsed time %5!s!, throughput %6!s!/Min.%0
//
#define AFRES_AFREST_RESTORE_EXCH_GRT_SUMMARY ((DWORD)0x40130054L)

//
// MessageId: AFRES_AFREST_RESTORE_EXCH_GRT_SOURCE
//
// MessageText:
//
// Restore Mailbox Item(s) from %1!s!.%0
//
#define AFRES_AFREST_RESTORE_EXCH_GRT_SOURCE ((DWORD)0x40130055L)

//
// MessageId: AFRES_AFREST_RESTORE_EXCH_GRT_LACK_DISK
//
// MessageText:
//
// Restore item(s) to disk failed due to lack of disk space,please ensure that restore destination volume( %1!s!)  has sufficient disk space.%0
//
#define AFRES_AFREST_RESTORE_EXCH_GRT_LACK_DISK ((DWORD)0xC0130056L)

//
// MessageId: AFRES_AFREST_EXCH_GRT_CHECK_DB_PATH
//
// MessageText:
//
// The Mounted DB Path is too long, please create registry HKEY_LOCAL_MACHINE\SOFTWARE\CARCserve Unified Data Protection\Engine\ExGRT\MountPoint, and assign MountPoint to folder path of short length.%0
//
#define AFRES_AFREST_EXCH_GRT_CHECK_DB_PATH ((DWORD)0xC0130057L)

//
// MessageId: AFRES_AFREST_EXCH_GRT_MAILBOX_OVER_QUOTA
//
// MessageText:
//
// The mailbox:%1!s! has exceeded the mailbox storage quota limit,some items may not be restored.%0
//
#define AFRES_AFREST_EXCH_GRT_MAILBOX_OVER_QUOTA ((DWORD)0x80130058L)

//
// MessageId: AFRES_AFREST_RESTORE_EXCH_GRT_TO_DISK_SUMMARY
//
// MessageText:
//
// %1!d! folder(s) %2!d! email(s)(%3!s!) from %4!d! mailbox(es) restored to disk, elapsed time %5!s!, throughput %6!s!/Min.%0
//
#define AFRES_AFREST_RESTORE_EXCH_GRT_TO_DISK_SUMMARY ((DWORD)0x40130059L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_DEST_HYPERV_SERVER
//
// MessageText:
//
// The virtual machine will be restored to the Hyper-V host %1!s!.%0
//
#define AFRES_AFREST_HYPERV_VM_DEST_HYPERV_SERVER ((DWORD)0x4013005AL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_CONNECT_SERVER_OK
//
// MessageText:
//
// Successfully connected to the Hyper-V host .%0
//
#define AFRES_AFREST_HYPERV_VM_CONNECT_SERVER_OK ((DWORD)0x4013005BL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_CONNECT_SERVER_FAILED
//
// MessageText:
//
// Failed to connect to the Hyper-V host %1!s!.%0
//
#define AFRES_AFREST_HYPERV_VM_CONNECT_SERVER_FAILED ((DWORD)0xC013005CL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_EXIT_WITHOUT_OWR
//
// MessageText:
//
// Virtual machine %1!s! already exists on Hyper-V host %2!s!. To recover this virtual machine, you must enable the Overwrite option.%0
//
#define AFRES_AFREST_HYPERV_VM_EXIT_WITHOUT_OWR ((DWORD)0xC013005DL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_FOUND_DUP_VM
//
// MessageText:
//
// Virtual machine %1!s! already exists on Hyper-V host %2!s!.%0
//
#define AFRES_AFREST_HYPERV_VM_FOUND_DUP_VM ((DWORD)0x4013005EL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_RENAME_VM
//
// MessageText:
//
// The virtual machine was renamed from %1!s! to %2!s!.%0
//
#define AFRES_AFREST_HYPERV_VM_RENAME_VM ((DWORD)0x4013005FL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_PREPARE_DISK_REP
//
// MessageText:
//
// Preparing the virtual disk recovery...%0
//
#define AFRES_AFREST_HYPERV_VM_PREPARE_DISK_REP ((DWORD)0x40130060L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_ADD_SCSI_CONTROLLER_FAILED
//
// MessageText:
//
// Failed to add the SCSI controllers for the virtual machine.%0
//
#define AFRES_AFREST_HYPERV_VM_ADD_SCSI_CONTROLLER_FAILED ((DWORD)0xC0130061L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_REP_DISK
//
// MessageText:
//
// Recovering virtual disk %1!u!...%0
//
#define AFRES_AFREST_HYPERV_VM_REP_DISK  ((DWORD)0x40130062L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_ATTACH_DISK
//
// MessageText:
//
// Attaching the disk %1!u! to the virtual machine.%0
//
#define AFRES_AFREST_HYPERV_VM_ATTACH_DISK ((DWORD)0x40130063L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_SET_PROCESSOR
//
// MessageText:
//
// Configuring the processor (count = %1!u!).%0
//
#define AFRES_AFREST_HYPERV_VM_SET_PROCESSOR ((DWORD)0x40130064L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_SET_MEMORY
//
// MessageText:
//
// Configuring the memory (size = %1!u! MB).%0
//
#define AFRES_AFREST_HYPERV_VM_SET_MEMORY ((DWORD)0x40130065L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_SET_NETWORK_ADAPTERS
//
// MessageText:
//
// Configuring the network adapters.%0
//
#define AFRES_AFREST_HYPERV_VM_SET_NETWORK_ADAPTERS ((DWORD)0x40130066L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_TO_LOWER_HYPERV
//
// MessageText:
//
// The virtual machine will be restored to an earlier version of the target Hyper-V server. (The target Hyper-V server version is %1!s!, while the original Hyper-V server version is %2!s!. Recovery job may fail in some cases, for more details please refer to the product document).%0
//
#define AFRES_AFREST_HYPERV_VM_TO_LOWER_HYPERV ((DWORD)0x80130067L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_DEPLOY_STUB
//
// MessageText:
//
// Deploying the restore utility on the Hyper-V host %1!s!.%0
//
#define AFRES_AFREST_HYPERV_VM_DEPLOY_STUB ((DWORD)0x40130068L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_DEPLOY_STUB_FAILED
//
// MessageText:
//
// Failed to deploy the restore utility. System error=[%1!s!]. Verify that \\%2!s!\%3!s! is accessible from the proxy machine.%0
//
#define AFRES_AFREST_HYPERV_VM_DEPLOY_STUB_FAILED ((DWORD)0xC0130069L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_STUB_CONNECTED
//
// MessageText:
//
// Successfully connected to the Hyper-V restore utility on host %1!s!.%0
//
#define AFRES_AFREST_HYPERV_VM_STUB_CONNECTED ((DWORD)0x4013006AL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_STUB_CONNECT_FAILED
//
// MessageText:
//
// Failed to connect to the Hyper-V restore utility on host %1!s!.%0
//
#define AFRES_AFREST_HYPERV_VM_STUB_CONNECT_FAILED ((DWORD)0xC013006BL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_CREATE_VDISK_FAILED
//
// MessageText:
//
// Failed to create the virtual disk [%1!s!]. Error=[%2!s!].%0
//
#define AFRES_AFREST_HYPERV_VM_CREATE_VDISK_FAILED ((DWORD)0xC013006CL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_WRITE_VDISK_FAILED
//
// MessageText:
//
// Failed to write the virtual disk [%1!s!]. Error=[%2!s!].%0
//
#define AFRES_AFREST_HYPERV_VM_WRITE_VDISK_FAILED ((DWORD)0xC013006DL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_CLOSE_VDISK_FAILED
//
// MessageText:
//
// Failed to close the virtual disk [%1!s!]. Error=[%2!s!].%0
//
#define AFRES_AFREST_HYPERV_VM_CLOSE_VDISK_FAILED ((DWORD)0xC013006EL)

//
// MessageId: AFRES_AFREST_ADGRT_BEGIN_RESTORE
//
// MessageText:
//
// Begin to restore Active Directory items.%0
//
#define AFRES_AFREST_ADGRT_BEGIN_RESTORE ((DWORD)0x4013006FL)

//
// MessageId: AFRES_AFREST_ADGRT_FINISH_RESTORE
//
// MessageText:
//
// Finished to restore Active Directory items.%0
//
#define AFRES_AFREST_ADGRT_FINISH_RESTORE ((DWORD)0x40130070L)

//
// MessageId: AFRES_AFREST_ADGRT_RESTORE_STATUS
//
// MessageText:
//
// Restored %1!d! objects %2!d! attributes, skipped %3!d! objects %4!d! attributes.%0
//
#define AFRES_AFREST_ADGRT_RESTORE_STATUS ((DWORD)0x40130071L)

//
// MessageId: AFRES_AFREST_ADGRT_OPEN_DATABASE
//
// MessageText:
//
// Open Active Directory database(%1!s!).%0
//
#define AFRES_AFREST_ADGRT_OPEN_DATABASE ((DWORD)0x40130072L)

//
// MessageId: AFRES_AFREST_ADGRT_OP_RENAME
//
// MessageText:
//
// The option to allow restoring of Renamed Objects is selected.%0
//
#define AFRES_AFREST_ADGRT_OP_RENAME     ((DWORD)0x40130073L)

//
// MessageId: AFRES_AFREST_ADGRT_OP_RENAME_SKIP
//
// MessageText:
//
// "The option to allow restoring of Renamed Objects is not selected.%0
//
#define AFRES_AFREST_ADGRT_OP_RENAME_SKIP ((DWORD)0x40130074L)

//
// MessageId: AFRES_AFREST_ADGRT_OP_MOVE
//
// MessageText:
//
// The option to allow restoring of Moved Objects is selected.%0
//
#define AFRES_AFREST_ADGRT_OP_MOVE       ((DWORD)0x40130075L)

//
// MessageId: AFRES_AFREST_ADGRT_OP_MOVE_SKIP
//
// MessageText:
//
// The option to allow restoring of Moved Objects is not selected.%0
//
#define AFRES_AFREST_ADGRT_OP_MOVE_SKIP  ((DWORD)0x40130076L)

//
// MessageId: AFRES_AFREST_ADGRT_OP_DELETE
//
// MessageText:
//
// The option to allow restoring of Deleted Objects is selected.%0
//
#define AFRES_AFREST_ADGRT_OP_DELETE     ((DWORD)0x40130077L)

//
// MessageId: AFRES_AFREST_ADGRT_OP_DELETE_SKIP
//
// MessageText:
//
// The option to allow restoring of Deleted Objects is not selected.%0
//
#define AFRES_AFREST_ADGRT_OP_DELETE_SKIP ((DWORD)0x40130078L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_READ_D2D_FAILED
//
// MessageText:
//
// The operation (open, read, or close) on the source backup disk file [%1!s!] has failed.%0
//
#define AFRES_AFREST_HYPERV_VM_READ_D2D_FAILED ((DWORD)0xC0130079L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_VCLOUD_CONNECT
//
// MessageText:
//
// Successfully connected to the VMware vCloud Director.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_VCLOUD_CONNECT ((DWORD)0x4013007AL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_VCLOUD_CONNECT_FAIL
//
// MessageText:
//
// Unable to connect to the VMware vCloud Director.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_VCLOUD_CONNECT_FAIL ((DWORD)0xC013007BL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_FOUND_DUPLICATE
//
// MessageText:
//
// The vApp %1!s! already exists in the destination Virtual Datacenter.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_FOUND_DUPLICATE ((DWORD)0x4013007CL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_EXIT_WITHOUT_OWR
//
// MessageText:
//
// The vApp %1!s! already exists in the destination Virtual Datacenter. To recover this vApp, you must enable the Overwrite option.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_EXIT_WITHOUT_OWR ((DWORD)0xC013007DL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_RENAME_VAPP
//
// MessageText:
//
// The vApp was renamed from %1!s! to %2!s!.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_RENAME_VAPP ((DWORD)0x4013007EL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_CREATE_VAPP
//
// MessageText:
//
// Creating new vApp...%0
//
#define AFRES_AFREST_VCLOUD_VAPP_CREATE_VAPP ((DWORD)0x4013007FL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_CREATE_VAPP_FAIL
//
// MessageText:
//
// Failed to create the vApp %1!s!. The vCloud system reported error: %2!s!.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_CREATE_VAPP_FAIL ((DWORD)0xC0130080L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_IMPORT_VM
//
// MessageText:
//
// Importing the virtual machine %1!s! into the vApp...%0
//
#define AFRES_AFREST_VCLOUD_VAPP_IMPORT_VM ((DWORD)0x40130081L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_IMPORT_VM_FAIL
//
// MessageText:
//
// Failed to import the virtual machine %1!s! into the vApp. The vCloud system reported error: %2!s!.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_IMPORT_VM_FAIL ((DWORD)0xC0130082L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_STARTED
//
// MessageText:
//
// Restore job %1!u! for the virtual machine %2!s! has started.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_STARTED ((DWORD)0x40130083L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_FINISHED
//
// MessageText:
//
// Restore job %1!u! for the virtual machine %2!s! successfully completed.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_FINISHED ((DWORD)0x40130084L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_FAILED
//
// MessageText:
//
// Restore job %1!u! for the virtual machine %2!s! failed.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_FAILED ((DWORD)0xC0130085L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_CANCELED
//
// MessageText:
//
// Restore job %1!u! for the virtual machine %2!s! was canceled.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_CANCELED ((DWORD)0x80130086L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_NOT_LAUNCHED
//
// MessageText:
//
// Restore job %1!u! for the virtual machine %2!s! was not launched.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_CHILD_JOB_NOT_LAUNCHED ((DWORD)0x80130087L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_DELETE_OLD_VAPP
//
// MessageText:
//
// Recovery was successfully completed. Delete the original vApp %1!s!(%2!s!).%0
//
#define AFRES_AFREST_VCLOUD_VAPP_DELETE_OLD_VAPP ((DWORD)0x40130088L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_DELETE_FAILED_VAPP
//
// MessageText:
//
// Delete the vApp that was unsuccessfully recovered.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_DELETE_FAILED_VAPP ((DWORD)0x40130089L)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_DELETE_VAPP_FAILED
//
// MessageText:
//
// Unable to delete the vApp %1!s!.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_DELETE_VAPP_FAILED ((DWORD)0x8013008AL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_POWER_ON
//
// MessageText:
//
// Powering on the recovered vApp...%0
//
#define AFRES_AFREST_VCLOUD_VAPP_POWER_ON ((DWORD)0x4013008BL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_POWER_ON_FAIL
//
// MessageText:
//
// Unable to power on the recovered vApp.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_POWER_ON_FAIL ((DWORD)0x8013008CL)

//
// MessageId: AFRES_AFREST_VCLOUD_VAPP_INCOMPLETE
//
// MessageText:
//
// The restore job was partially completed. Some virtual machines in the vApp were not successfully recovered.%0
//
#define AFRES_AFREST_VCLOUD_VAPP_INCOMPLETE ((DWORD)0x4013008DL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_REGISTER2CLUSTER
//
// MessageText:
//
// Adding the virtual machine to the cluster...%0
//
#define AFRES_AFREST_HYPERV_VM_REGISTER2CLUSTER ((DWORD)0x4013008EL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_REGISTER2CLUSTER_FAILED
//
// MessageText:
//
// Unable to add the virtual machine to the cluster.%0
//
#define AFRES_AFREST_HYPERV_VM_REGISTER2CLUSTER_FAILED ((DWORD)0x8013008FL)

//
// MessageId: AFRES_AFREST_HYPERV_VM_UNABLECONNECT2SWITCH
//
// MessageText:
//
// Unable to connect the network adapter '%1!s!' to the virtual switch.%0
//
#define AFRES_AFREST_HYPERV_VM_UNABLECONNECT2SWITCH ((DWORD)0x80130090L)

//
// MessageId: AFRES_AFREST_VMR_DEL_OLD_VM_ON_HYPERVISOR
//
// MessageText:
//
// Delete the original virtual machine %1!s! on host %2!s!.%0
//
#define AFRES_AFREST_VMR_DEL_OLD_VM_ON_HYPERVISOR ((DWORD)0x40130091L)

//
// MessageId: AFRES_AFREST_HYPERV_VM_VOLUME_NOT_FOUND
//
// MessageText:
//
// Failed to find the specified path [%1!s!]. Verify the volume state on the Hyper-V host is mounted and try the recovery again.%0
//
#define AFRES_AFREST_HYPERV_VM_VOLUME_NOT_FOUND ((DWORD)0xC0130092L)

//
// MessageId: AFRES_AFREST_VMR_DUP_UUID_EXISTS
//
// MessageText:
//
// Unable to restore the instance UUID for the new virtual machine because another virtual machine named [%1!s!] with the same instance UUID already exists in the inventory.%0
//
#define AFRES_AFREST_VMR_DUP_UUID_EXISTS ((DWORD)0x80130093L)

//
// MessageId: AFRES_AFREST_VMR_DUP_VM_EXISTS_ON_ANOTHER_ESX
//
// MessageText:
//
// The virtual machine [%1!s!] conflicts with a virtual machine with the same name on another ESX server [%2!s!].%0
//
#define AFRES_AFREST_VMR_DUP_VM_EXISTS_ON_ANOTHER_ESX ((DWORD)0xC0130094L)

//
// MessageId: AFRES_AFREST_VMR_CHECKING_CONFLICTS
//
// MessageText:
//
// Checking conflicts in the inventory...%0
//
#define AFRES_AFREST_VMR_CHECKING_CONFLICTS ((DWORD)0x40130095L)

//
// MessageId: AFRES_AFREST_VMR_GEN_NEW_VM_ID
//
// MessageText:
//
// The option, Generate new Virtual Machine instance UUID, is enabled.%0
//
#define AFRES_AFREST_VMR_GEN_NEW_VM_ID   ((DWORD)0x40130096L)

//
// MessageId: AFRES_AFREST_READ_DATA_ERROR
//
// MessageText:
//
// Read data failed. Error Code:%1!d!.%0
//
#define AFRES_AFREST_READ_DATA_ERROR     ((DWORD)0xC0130097L)

//
// MessageId: AFRES_AFREST_REOPEN_DEVICE
//
// MessageText:
//
// Try to reopen the device.%0
//
#define AFRES_AFREST_REOPEN_DEVICE       ((DWORD)0x80130098L)

//
// MessageId: AFRES_AFREST_RUN_CHECKDISK
//
// MessageText:
//
// Repairing volume [%1!s!]. Failed to access the volume after recovery. There may be errors on the volume and the chkdsk.exe has been launched to scan the volume. Please do not close the command console windows before the chkdsk.exe completes this scan.%0
//
#define AFRES_AFREST_RUN_CHECKDISK       ((DWORD)0x80130099L)

//
// MessageId: AFRES_AFREST_CANNOT_EXTEND_REFS
//
// MessageText:
//
// When you restore a ReFS volume from a 4KB disk to the 512 disk, BMR cannot extend size of volume [%1!s!] to %2!u! MB. You need to manually extend it after reboot machine.%0
//
#define AFRES_AFREST_CANNOT_EXTEND_REFS  ((DWORD)0x8013009AL)

//
// MessageId: AFRES_AFREST_VMR_THICK_PROVOSION_NOT_SUPPORTED
//
// MessageText:
//
// Thick provision is not supported in some datastores. The virtual disks will be restored as thin provision to the datastores.%0
//
#define AFRES_AFREST_VMR_THICK_PROVOSION_NOT_SUPPORTED ((DWORD)0x8013009BL)

//
// MessageId: AFRES_AFREST_NO_ENOUGH_SPACE_FOR_BITMAP
//
// MessageText:
//
// The ram disk does not have enough space to save bitmap files. Specify a share folder in X:\windows\system32\DR\BmrCfg.ini, and then retry BMR.%0
//
#define AFRES_AFREST_NO_ENOUGH_SPACE_FOR_BITMAP ((DWORD)0xC013009CL)

//
// MessageId: AFRES_AFREST_NO_WRITE_ACCESS
//
// MessageText:
//
// Could not access the folder [%1!s!] or the user does not have the write privilege.?%0
//
#define AFRES_AFREST_NO_WRITE_ACCESS     ((DWORD)0xC013009DL)

 // The following are the message definitions for Rps
//
// MessageId: RPS_IMJ_010
//
// MessageText:
//
// %0
//
#define RPS_IMJ_010                      ((DWORD)0xC0140001L)

//
// MessageId: RPS_REP_000
//
// MessageText:
//
// Replication job started for node %1!s! to the destination server (server = %2!s!, data store = %3!s!).%0
//
#define RPS_REP_000                      ((DWORD)0x40140002L)

//
// MessageId: RPS_REP_001
//
// MessageText:
//
// The source server is %1!s! and the source data store is %2!s!.%0
//
#define RPS_REP_001                      ((DWORD)0x40140003L)

//
// MessageId: RPS_REP_002
//
// MessageText:
//
// Replication job failed for node %1!s! because machine was shut down.%0
//
#define RPS_REP_002                      ((DWORD)0xC0140004L)

//
// MessageId: RPS_REP_003
//
// MessageText:
//
// Replication of session %1!u! (total size = %2!s!) successfully completed.%0
//
#define RPS_REP_003                      ((DWORD)0x40140005L)

//
// MessageId: RPS_REP_004
//
// MessageText:
//
// Failed to replicate session %1!u! (total size = %2!s!, replicated size = %3!s!).%0
//
#define RPS_REP_004                      ((DWORD)0xC0140006L)

//
// MessageId: RPS_REP_005
//
// MessageText:
//
// An error occurred while connecting to the destination server. Error: '%1!s!'.%0
//
#define RPS_REP_005                      ((DWORD)0xC0140007L)

//
// MessageId: RPS_REP_006
//
// MessageText:
//
// Replication job crashed.%0
//
#define RPS_REP_006                      ((DWORD)0xC0140008L)

//
// MessageId: RPS_REP_007
//
// MessageText:
//
// Replicating session %1!u! to the destination data store ...%0
//
#define RPS_REP_007                      ((DWORD)0x40140009L)

//
// MessageId: RPS_REP_008
//
// MessageText:
//
// Replication from deduplication enabled data store %1!s! to non-deduplication enabled data store %2!s! is not supported.%0
//
#define RPS_REP_008                      ((DWORD)0xC014000AL)

//
// MessageId: RPS_REP_009
//
// MessageText:
//
// Failed to get the GDD configuration file from the destination server.%0
//
#define RPS_REP_009                      ((DWORD)0xC014000BL)

//
// MessageId: RPS_REP_010
//
// MessageText:
//
// x .%0
//
#define RPS_REP_010                      ((DWORD)0x4014000CL)

//
// MessageId: RPS_REP_011
//
// MessageText:
//
// x .%0
//
#define RPS_REP_011                      ((DWORD)0x4014000DL)

//
// MessageId: RPS_REP_012
//
// MessageText:
//
// x .%0
//
#define RPS_REP_012                      ((DWORD)0x4014000EL)

//
// MessageId: RPS_REP_013
//
// MessageText:
//
// %0
//
#define RPS_REP_013                      ((DWORD)0x4014000FL)

//
// MessageId: RPS_REP_014
//
// MessageText:
//
// Replication from an encrypted data store to a non-encrypted data store is not supported.%0
//
#define RPS_REP_014                      ((DWORD)0xC0140010L)

//
// MessageId: RPS_REP_015
//
// MessageText:
//
// x .%0
//
#define RPS_REP_015                      ((DWORD)0x40140011L)

//
// MessageId: RPS_REP_016
//
// MessageText:
//
// Replication job is skipped because there are no new recovery points to be replicated.%0
//
#define RPS_REP_016                      ((DWORD)0x80140012L)

//
// MessageId: RPS_REP_017
//
// MessageText:
//
// Replication job was canceled on the source server %1!s!.%0
//
#define RPS_REP_017                      ((DWORD)0x80140013L)

//
// MessageId: RPS_REP_018
//
// MessageText:
//
// Replication job was canceled on the destination server %1!s!.%0
//
#define RPS_REP_018                      ((DWORD)0x80140014L)

//
// MessageId: RPS_REP_019
//
// MessageText:
//
// Connecting to the destination server (server = %1!s!, port = %2!u!) ...%0
//
#define RPS_REP_019                      ((DWORD)0x40140015L)

//
// MessageId: RPS_REP_020
//
// MessageText:
//
// Preparing the replication session list ...%0
//
#define RPS_REP_020                      ((DWORD)0x40140016L)

//
// MessageId: RPS_REP_021
//
// MessageText:
//
// Start to replicate session %1!u!.%0
//
#define RPS_REP_021                      ((DWORD)0x40140017L)

//
// MessageId: RPS_REP_022
//
// MessageText:
//
// Start to replicate sessions. The session range to replicate is from [start session = %1!u!] to [end session = %2!u!].%0
//
#define RPS_REP_022                      ((DWORD)0x40140018L)

//
// MessageId: RPS_REP_023
//
// MessageText:
//
// Replicating session %1!u! from the source data store ...%0
//
#define RPS_REP_023                      ((DWORD)0x40140019L)

//
// MessageId: RPS_REP_024
//
// MessageText:
//
// Replication job failed for node %1!s!.%0
//
#define RPS_REP_024                      ((DWORD)0xC014001AL)

//
// MessageId: RPS_REP_025
//
// MessageText:
//
// Replication job started for node %1!s! from the source server (server = %2!s!, data store = %3!s!).%0
//
#define RPS_REP_025                      ((DWORD)0x4014001BL)

//
// MessageId: RPS_REP_026
//
// MessageText:
//
// Only catalog files for session %1!u! will be replicated to the destination data store.%0
//
#define RPS_REP_026                      ((DWORD)0x8014001CL)

//
// MessageId: RPS_REP_027
//
// MessageText:
//
// Failed to replicate catalog files for session %1!u! (total size = %2!s!, replicated size = %3!s!).%0
//
#define RPS_REP_027                      ((DWORD)0xC014001DL)

//
// MessageId: RPS_REP_028
//
// MessageText:
//
// Only catalog files for session %1!u! will be replicated from the source data store.%0
//
#define RPS_REP_028                      ((DWORD)0x8014001EL)

//
// MessageId: RPS_REP_029
//
// MessageText:
//
// There is no session for node %1!s!.%0
//
#define RPS_REP_029                      ((DWORD)0xC014001FL)

//
// MessageId: RPS_REP_030
//
// MessageText:
//
// Failed to get session list for node %1!s!.%0
//
#define RPS_REP_030                      ((DWORD)0xC0140020L)

//
// MessageId: RPS_REP_031
//
// MessageText:
//
// Failed to replicate the file: %1!s!. Error: '%2!s!'. Insufficient free disk space where ^AU_ProductName_SERVER_SHORT^ is installed or at the destination data store.%0
//
#define RPS_REP_031                      ((DWORD)0xC0140021L)

//
// MessageId: RPS_REP_032
//
// MessageText:
//
// The destination server is %1!s! and the destination data store is %2!s!.%0
//
#define RPS_REP_032                      ((DWORD)0x40140022L)

//
// MessageId: RPS_REP_033
//
// MessageText:
//
// Replication job skipped because the node data is consistent between the source data store and the destination data store.%0
//
#define RPS_REP_033                      ((DWORD)0xC0140023L)

//
// MessageId: RPS_REP_034
//
// MessageText:
//
// Failed to access the data store (%1!s!\%2!s!).%0
//
#define RPS_REP_034                      ((DWORD)0xC0140024L)

//
// MessageId: RPS_REP_035
//
// MessageText:
//
// Failed to replicate the file: %1!s!. Error: '%2!s!'.%0
//
#define RPS_REP_035                      ((DWORD)0xC0140025L)

//
// MessageId: RPS_REP_036
//
// MessageText:
//
// Failed to lock session %1!u!. Error: '%2!s!'.%0
//
#define RPS_REP_036                      ((DWORD)0xC0140026L)

//
// MessageId: RPS_REP_037
//
// MessageText:
//
// Replication job finished successfully for node %1!s!.%0
//
#define RPS_REP_037                      ((DWORD)0x40140027L)

//
// MessageId: RPS_REP_038
//
// MessageText:
//
// Replicated %1!s! in %2!s! with an average throughput of %3!s!/Min.%0
//
#define RPS_REP_038                      ((DWORD)0x40140028L)

//
// MessageId: RPS_REP_039
//
// MessageText:
//
// %1!s! could not be replicated and will be processed during next replication.%0
//
#define RPS_REP_039                      ((DWORD)0xC0140029L)

//
// MessageId: RPS_REP_040
//
// MessageText:
//
// x .%0
//
#define RPS_REP_040                      ((DWORD)0x4014002AL)

//
// MessageId: RPS_REP_041
//
// MessageText:
//
// x .%0
//
#define RPS_REP_041                      ((DWORD)0x4014002BL)

//
// MessageId: RPS_REP_042
//
// MessageText:
//
// Failed to lock session %1!u!. The session has already been locked by computers: %2!s!.%0
//
#define RPS_REP_042                      ((DWORD)0xC014002CL)

//
// MessageId: RPS_REP_043
//
// MessageText:
//
// The amount of data actually transferred over the network was %1!s!, and the average network throughput was %2!s!.%0
//
#define RPS_REP_043                      ((DWORD)0x4014002DL)

//
// MessageId: RPS_REP_044
//
// MessageText:
//
// The amount of space saved due to deduplication and compression is %1!s!%%. %2!s! has been written to the disk.%0
//
#define RPS_REP_044                      ((DWORD)0x4014002EL)

//
// MessageId: RPS_REP_045
//
// MessageText:
//
// The amount of space saved due to deduplication is %1!s!%%. %2!s! has been written to the disk.%0
//
#define RPS_REP_045                      ((DWORD)0x4014002FL)

//
// MessageId: RPS_REP_046
//
// MessageText:
//
// The amount of space saved due to compression is %1!s!%%. %2!s! has been written to the disk.%0
//
#define RPS_REP_046                      ((DWORD)0x40140030L)

//
// MessageId: RPS_REP_047
//
// MessageText:
//
// %1!s! has been written to the disk.%0
//
#define RPS_REP_047                      ((DWORD)0x40140031L)

//
// MessageId: RPS_REP_048
//
// MessageText:
//
// Source RPS error: %1!s!.%0
//
#define RPS_REP_048                      ((DWORD)0xC0140032L)

//
// MessageId: RPS_REP_049
//
// MessageText:
//
// Source RPS warning: %1!s!.%0
//
#define RPS_REP_049                      ((DWORD)0x40140033L)

//
// MessageId: RPS_REP_050
//
// MessageText:
//
// Another job is currently running for the node to the same destination data store.%0
//
#define RPS_REP_050                      ((DWORD)0x80140034L)

//
// MessageId: RPS_REP_051
//
// MessageText:
//
// The source is from %1!s!.%0
//
#define RPS_REP_051                      ((DWORD)0x80140035L)

//
// MessageId: AFRES_RPS_SEEDING_000
//
// MessageText:
//
// RPS Jumpstart job started for node %1!s! to the destination server (server = %2!s!, data store = %3!s!).%0
//
#define AFRES_RPS_SEEDING_000            ((DWORD)0x40140036L)

//
// MessageId: AFRES_RPS_SEEDING_001
//
// MessageText:
//
// RPS Jumpstart job started for node %1!s! from %2!s!.%0
//
#define AFRES_RPS_SEEDING_001            ((DWORD)0x40140037L)

//
// MessageId: AFRES_RPS_SEEDING_002
//
// MessageText:
//
// RPS Jumpstart job crashed.%0
//
#define AFRES_RPS_SEEDING_002            ((DWORD)0xC0140038L)

//
// MessageId: AFRES_RPS_SEEDING_003
//
// MessageText:
//
// RPS Jumpstart job was canceled on the source server %1!s!.%0
//
#define AFRES_RPS_SEEDING_003            ((DWORD)0x80140039L)

//
// MessageId: AFRES_RPS_SEEDING_004
//
// MessageText:
//
// RPS Jumpstart job was canceled on the destination server %1!s!.%0
//
#define AFRES_RPS_SEEDING_004            ((DWORD)0x8014003AL)

//
// MessageId: AFRES_RPS_SEEDING_005
//
// MessageText:
//
// RPS Jumpstart job failed for node %1!s!.%0
//
#define AFRES_RPS_SEEDING_005            ((DWORD)0xC014003BL)

//
// MessageId: AFRES_RPS_SEEDING_006
//
// MessageText:
//
// RPS Jumpstart job finished successfully for node %1!s!.%0
//
#define AFRES_RPS_SEEDING_006            ((DWORD)0x4014003CL)

//
// MessageId: AFRES_RPS_SEEDING_007
//
// MessageText:
//
// Data for node %1!s! is already included in the target data store. As a result, the Jumpstart job will be skipped.%0
//
#define AFRES_RPS_SEEDING_007            ((DWORD)0xC014003DL)

//
// MessageId: AFRES_RPS_SEEDING_008
//
// MessageText:
//
// RPS Jumpstart job is skipped because there are no new recovery points to be replicated.%0
//
#define AFRES_RPS_SEEDING_008            ((DWORD)0xC014003EL)

//
// MessageId: RPS_CID2D_000
//
// MessageText:
//
// %0
//
#define RPS_CID2D_000                    ((DWORD)0xC014003FL)

//
// MessageId: RPS_CID2D_001
//
// MessageText:
//
// The parameter is incorrect.%0
//
#define RPS_CID2D_001                    ((DWORD)0xC0140040L)

//
// MessageId: RPS_CID2D_002
//
// MessageText:
//
// Create File failed.%0
//
#define RPS_CID2D_002                    ((DWORD)0xC0140041L)

//
// MessageId: RPS_CID2D_003
//
// MessageText:
//
// Read D2D FOOTER failed.%0
//
#define RPS_CID2D_003                    ((DWORD)0xC0140042L)

//
// MessageId: RPS_CID2D_004
//
// MessageText:
//
// Invalid disk type in D2D FOOTER.%0
//
#define RPS_CID2D_004                    ((DWORD)0xC0140043L)

//
// MessageId: RPS_CID2D_005
//
// MessageText:
//
// Read D2D HEADER failed.%0
//
#define RPS_CID2D_005                    ((DWORD)0xC0140044L)

//
// MessageId: RPS_CID2D_006
//
// MessageText:
//
// Checksum error while read D2D.%0
//
#define RPS_CID2D_006                    ((DWORD)0xC0140045L)

//
// MessageId: RPS_CID2D_007
//
// MessageText:
//
// Read BAT of D2D failed.%0
//
#define RPS_CID2D_007                    ((DWORD)0xC0140046L)

//
// MessageId: RPS_CID2D_008
//
// MessageText:
//
// Read bitmap of D2D failed.%0
//
#define RPS_CID2D_008                    ((DWORD)0xC0140047L)

//
// MessageId: RPS_CID2D_009
//
// MessageText:
//
// Invalid MAGIC number in D2D.%0
//
#define RPS_CID2D_009                    ((DWORD)0xC0140048L)

//
// MessageId: RPS_CID2D_00A
//
// MessageText:
//
// Create Index file failed.%0
//
#define RPS_CID2D_00A                    ((DWORD)0xC0140049L)

//
// MessageId: RPS_CID2D_00B
//
// MessageText:
//
// More input buffer is needed.%0
//
#define RPS_CID2D_00B                    ((DWORD)0xC014004AL)

//
// MessageId: RPS_CID2D_00C
//
// MessageText:
//
// Invalid path type while set D2D parent path.%0
//
#define RPS_CID2D_00C                    ((DWORD)0xC014004BL)

//
// MessageId: RPS_CID2D_00D
//
// MessageText:
//
// Invalid path type while get D2D parent path.%0
//
#define RPS_CID2D_00D                    ((DWORD)0xC014004CL)

//
// MessageId: RPS_CID2D_00E
//
// MessageText:
//
// D2D Parent path not exist%0
//
#define RPS_CID2D_00E                    ((DWORD)0xC014004DL)

//
// MessageId: RPS_CID2D_00F
//
// MessageText:
//
// This is a FULL D2D, cannot to get parent path.%0
//
#define RPS_CID2D_00F                    ((DWORD)0xC014004EL)

//
// MessageId: RPS_CID2D_010
//
// MessageText:
//
// This is a FULL D2D, cannot set parent path.%0
//
#define RPS_CID2D_010                    ((DWORD)0xC014004FL)

//
// MessageId: RPS_CID2D_011
//
// MessageText:
//
// This is a FULL D2D, cannot set parent timestamp.%0
//
#define RPS_CID2D_011                    ((DWORD)0xC0140050L)

//
// MessageId: RPS_CID2D_012
//
// MessageText:
//
// This ia a FULL D2D, cannot get parent timestamp.%0
//
#define RPS_CID2D_012                    ((DWORD)0xC0140051L)

//
// MessageId: RPS_CID2D_013
//
// MessageText:
//
// Parent timestamp not exists.%0
//
#define RPS_CID2D_013                    ((DWORD)0xC0140052L)

//
// MessageId: RPS_CID2D_014
//
// MessageText:
//
// Write D2D file HEADER failed.%0
//
#define RPS_CID2D_014                    ((DWORD)0xC0140053L)

//
// MessageId: RPS_CID2D_015
//
// MessageText:
//
// Set file position failed.%0
//
#define RPS_CID2D_015                    ((DWORD)0xC0140054L)

//
// MessageId: RPS_CID2D_016
//
// MessageText:
//
// Write D2D FOOTER failed.%0
//
#define RPS_CID2D_016                    ((DWORD)0xC0140055L)

//
// MessageId: RPS_CID2D_017
//
// MessageText:
//
// Cannot position to unused BAT entry.%0
//
#define RPS_CID2D_017                    ((DWORD)0xC0140056L)

//
// MessageId: RPS_CID2D_018
//
// MessageText:
//
// Not enough memory is available.%0
//
#define RPS_CID2D_018                    ((DWORD)0xC0140057L)

//
// MessageId: RPS_CID2D_019
//
// MessageText:
//
// Not enough free disk space to split the idx file.%0
//
#define RPS_CID2D_019                    ((DWORD)0xC0140058L)

//
// MessageId: RPS_CID2D_01A
//
// MessageText:
//
// Internal error.%0
//
#define RPS_CID2D_01A                    ((DWORD)0xC0140059L)

//
// MessageId: RPS_CID2D_01B
//
// MessageText:
//
// Read D2D data failed.%0
//
#define RPS_CID2D_01B                    ((DWORD)0xC014005AL)

//
// MessageId: RPS_CID2D_01C
//
// MessageText:
//
// Update MTA file failed.%0
//
#define RPS_CID2D_01C                    ((DWORD)0xC014005BL)

//
// MessageId: RPS_CID2D_01D
//
// MessageText:
//
// Failed to lock session on destination side.%0
//
#define RPS_CID2D_01D                    ((DWORD)0xC014005CL)

//
// MessageId: RPS_CID2D_050
//
// MessageText:
//
// More data range is available.%0
//
#define RPS_CID2D_050                    ((DWORD)0x4014005DL)

//
// MessageId: RPS_CID2D_051
//
// MessageText:
//
// The authentication process failed on the destination server.%0
//
#define RPS_CID2D_051                    ((DWORD)0xC014005EL)

//
// MessageId: RPS_CID2D_052
//
// MessageText:
//
// The job count exceeded the specified limit on the destination server.%0
//
#define RPS_CID2D_052                    ((DWORD)0x4014005FL)

//
// MessageId: RPS_CID2D_053
//
// MessageText:
//
// The plan was paused on the destination server.%0
//
#define RPS_CID2D_053                    ((DWORD)0x40140060L)

//
// MessageId: RPS_CID2D_054
//
// MessageText:
//
// Failed to run another job, because a purge job is currently running.%0
//
#define RPS_CID2D_054                    ((DWORD)0x40140061L)

//
// MessageId: RPS_CID2D_060
//
// MessageText:
//
// The specified timeout period expired while communicating with the web service on the destination server.%0
//
#define RPS_CID2D_060                    ((DWORD)0x40140062L)

//
// MessageId: RPS_CID2D_UNSPECIFIED_ERROR
//
// MessageText:
//
// Unspecified error.%0
//
#define RPS_CID2D_UNSPECIFIED_ERROR      ((DWORD)0xC0140063L)

//
// MessageId: RPS_BACKUP_DESTINATION
//
// MessageText:
//
// Backup destination is ^AU_ProductName_SERVER^ [%1!s!], data store [%2!s!].%0
//
#define RPS_BACKUP_DESTINATION           ((DWORD)0x40140064L)

//
// MessageId: AFRES_RPS_NODE_PURGE_JOB_INCOMPLETE
//
// MessageText:
//
// Node data purge job is incomplete..%0
//
#define AFRES_RPS_NODE_PURGE_JOB_INCOMPLETE ((DWORD)0x40140065L)

//
// MessageId: RPS_REP_052
//
// MessageText:
//
// Failed to lock session %1!u!. The session has already been locked by computer: %2!s!.%0
//
#define RPS_REP_052                      ((DWORD)0x80140066L)

//
// MessageId: RPS_REP_053
//
// MessageText:
//
// SSL connection client is using protocol %1!s!, cipher %2!s!, cipher bits: %3!u!.%0
//
#define RPS_REP_053                      ((DWORD)0x40140067L)

//
// MessageId: RPS_CID2D_061
//
// MessageText:
//
// Replication job was delayed due to ongoing data store maintenance (merge is still in progress) . Make up replication job will be started during next backup cycle.%0
//
#define RPS_CID2D_061                    ((DWORD)0x40140068L)

//
// MessageId: RPS_REP_054
//
// MessageText:
//
// The version [%1!s!] on source RPS [%2!s!] is not identical to the version [%3!s!] on destination RPS [%4!s!]. Please upgrade the version of the RPS.?%0
//
#define RPS_REP_054                      ((DWORD)0x80140069L)

//
// MessageId: RPS_REP_055
//
// MessageText:
//
// The volume [%1!s!] on the RPS server [%2!s!] does not have adequate free space for the replication job.?%0
//
#define RPS_REP_055                      ((DWORD)0xC014006AL)

 // The following are the message definitions for Update
//
// MessageId: AUTOUPDATE_FILE_LOCATE_FAILED
//
// MessageText:
//
// Failed to locate file: %1!s!.%0
//
#define AUTOUPDATE_FILE_LOCATE_FAILED    ((DWORD)0xC0150001L)

//
// MessageId: AUTOUPDATE_SECOND_INSTANCE
//
// MessageText:
//
// Another instance of Updates process is currently running.%0
//
#define AUTOUPDATE_SECOND_INSTANCE       ((DWORD)0xC0150002L)

//
// MessageId: AUTOUPDATE_FILE_CORRUPTED
//
// MessageText:
//
// File %1!s! is corrupted.%0
//
#define AUTOUPDATE_FILE_CORRUPTED        ((DWORD)0xC0150003L)

//
// MessageId: AUTOUPDATE_FILE_NOT_SIGNED
//
// MessageText:
//
// File %1!s! is not signed.%0
//
#define AUTOUPDATE_FILE_NOT_SIGNED       ((DWORD)0x80150004L)

//
// MessageId: AUTOUPDATE_ENV_CHECK_FAILED
//
// MessageText:
//
// Environment check failed.%0
//
#define AUTOUPDATE_ENV_CHECK_FAILED      ((DWORD)0xC0150005L)

//
// MessageId: AUTOUPDATE_SPACE_INSUFFICIENT
//
// MessageText:
//
// Required space is not available.%0
//
#define AUTOUPDATE_SPACE_INSUFFICIENT    ((DWORD)0x80150006L)

//
// MessageId: AUTOUPDATE_INVALID_PATH
//
// MessageText:
//
// Invalid path %1!s! for updates.%0
//
#define AUTOUPDATE_INVALID_PATH          ((DWORD)0x80150007L)

//
// MessageId: AUTOUPDATE_WRITE_FAILED
//
// MessageText:
//
// Failed to write file. Required disk space may not be available.%0
//
#define AUTOUPDATE_WRITE_FAILED          ((DWORD)0xC0150008L)

//
// MessageId: AUTOUPDATE_MEMORY_ALLOC_FAILED
//
// MessageText:
//
// Memory allocation failed.%0
//
#define AUTOUPDATE_MEMORY_ALLOC_FAILED   ((DWORD)0x80150009L)

//
// MessageId: AUTOUPDATE_NTWRK_CONCT_FAILED
//
// MessageText:
//
// %1!s!. Failed to connect to network: %2!s!.%0
//
#define AUTOUPDATE_NTWRK_CONCT_FAILED    ((DWORD)0xC015000AL)

//
// MessageId: AUTOUPDATE_DWNLD_FAILED
//
// MessageText:
//
// Download failed. Maximum number of retry attempts reached.%0
//
#define AUTOUPDATE_DWNLD_FAILED          ((DWORD)0xC015000BL)

//
// MessageId: AUTOUPDATE_DIRECTRY_CREATE_FAILED
//
// MessageText:
//
// Failed to create download directory.%0
//
#define AUTOUPDATE_DIRECTRY_CREATE_FAILED ((DWORD)0xC015000CL)

//
// MessageId: AUTOUPDATE_STATUSXML_GEN_FAILED
//
// MessageText:
//
// Failed to generate status.xml file.%0
//
#define AUTOUPDATE_STATUSXML_GEN_FAILED  ((DWORD)0xC015000DL)

//
// MessageId: AUTOUPDATE_INTERNET_ERROR_CODE
//
// MessageText:
//
// Internet Error Code: %1!d!.%0
//
#define AUTOUPDATE_INTERNET_ERROR_CODE   ((DWORD)0xC015000EL)

//
// MessageId: AUTOUPDATE_TIMEOUT_FAILURE
//
// MessageText:
//
// A timeout failure has occurred.%0
//
#define AUTOUPDATE_TIMEOUT_FAILURE       ((DWORD)0xC015000FL)

//
// MessageId: AUTOUPDATE_LOGON_FAILED
//
// MessageText:
//
// Failed to connect and log on to HTTP server %1!s!.%0
//
#define AUTOUPDATE_LOGON_FAILED          ((DWORD)0xC0150010L)

//
// MessageId: AUTOUPDATE_SERVER_RESPONSE_COULD_NOT_PARSED
//
// MessageText:
//
// Server %1!s! response could not be parsed. Contact your server administrator.%0
//
#define AUTOUPDATE_SERVER_RESPONSE_COULD_NOT_PARSED ((DWORD)0xC0150011L)

//
// MessageId: AUTOUPDATE_SERVER_CONNECTION_FAILED
//
// MessageText:
//
// Failed to connect to server %1!s!. Verify that the server is up and running and the proxy settings and server IP/Name are correct.%0
//
#define AUTOUPDATE_SERVER_CONNECTION_FAILED ((DWORD)0xC0150012L)

//
// MessageId: AUTOUPDATE_SERVER_CONNECTION_TERMINATED
//
// MessageText:
//
// Connection with server %1!s! has been terminated.%0
//
#define AUTOUPDATE_SERVER_CONNECTION_TERMINATED ((DWORD)0xC0150013L)

//
// MessageId: AUTOUPDATE_REQUIRES_CLIENT_AUTH
//
// MessageText:
//
// Server %1!s! is requesting client authentication.%0
//
#define AUTOUPDATE_REQUIRES_CLIENT_AUTH  ((DWORD)0xC0150014L)

//
// MessageId: AUTOUPDATE_URL_NOT_RECONGNIZED
//
// MessageText:
//
// The URL scheme could not be recognized, or is not supported: %1!s!.%0
//
#define AUTOUPDATE_URL_NOT_RECONGNIZED   ((DWORD)0xC0150015L)

//
// MessageId: AUTOUPDATE_SERVER_NOT_RESOLVED
//
// MessageText:
//
// Server %1!s! could not be resolved. Verify the proxy server settings are correct and no network errors exist.%0
//
#define AUTOUPDATE_SERVER_NOT_RESOLVED   ((DWORD)0xC0150016L)

//
// MessageId: AUTOUPDATE_UNEXPTECTED_ERROR
//
// MessageText:
//
// "Server encountered an unexpected condition which prevented it from completing the request. Check the D2DUpdates.Log file for more information or contact arcserve support.%0
//
#define AUTOUPDATE_UNEXPTECTED_ERROR     ((DWORD)0xC0150017L)

//
// MessageId: AUTOUPDATE_SERVER_TEMPORALIY_NOTAVAILABLE
//
// MessageText:
//
// Server %1!s! may be temporarily unavailable, or there could be a network problem.%0
//
#define AUTOUPDATE_SERVER_TEMPORALIY_NOTAVAILABLE ((DWORD)0xC0150018L)

//
// MessageId: AUTOUPDATE_INVALID_URL
//
// MessageText:
//
// Invalid URL: %1!s!%0
//
#define AUTOUPDATE_INVALID_URL           ((DWORD)0xC0150019L)

//
// MessageId: AUTOUPDATE_CONTACT_CA_TECH
//
// MessageText:
//
// Please contact arcserve support.%0
//
#define AUTOUPDATE_CONTACT_CA_TECH       ((DWORD)0xC015001AL)

//
// MessageId: AUTOUPDATE_INSTALL_SUCCESS
//
// MessageText:
//
// New update (Version %1!d!) has been successfully installed.%0
//
#define AUTOUPDATE_INSTALL_SUCCESS       ((DWORD)0x4015001BL)

//
// MessageId: AUTOUPDATE_DOWNLOAD_SUCCESS
//
// MessageText:
//
// Download of new update  was successful: %1!s!%0
//
#define AUTOUPDATE_DOWNLOAD_SUCCESS      ((DWORD)0x4015001CL)

//
// MessageId: AUTOUPDATE_CHECKUPDATE_SUCCESS
//
// MessageText:
//
// Check for update was successful.%0
//
#define AUTOUPDATE_CHECKUPDATE_SUCCESS   ((DWORD)0x4015001DL)

//
// MessageId: AUTOUPDATE_NONE_SERVER_AVAILABLE
//
// MessageText:
//
// None of the configured servers are available. Please verify D2DUpdates.Log for more details.%0
//
#define AUTOUPDATE_NONE_SERVER_AVAILABLE ((DWORD)0xC015001EL)

//
// MessageId: AUTOUPDATE_NO_NEW_UPDATE_FOUND
//
// MessageText:
//
// ^AU_ProductName_AGENT^ is up to date.%0
//
#define AUTOUPDATE_NO_NEW_UPDATE_FOUND   ((DWORD)0x4015001FL)

//
// MessageId: AUTOUPDATE_PREFIX
//
// MessageText:
//
// Updates:%0
//
#define AUTOUPDATE_PREFIX                ((DWORD)0x40150020L)

//
// MessageId: AUTOUPDATE_LATEST_UPDATE_ALRDY_DOWNLODED
//
// MessageText:
//
// Latest available update has already been downloaded.%0
//
#define AUTOUPDATE_LATEST_UPDATE_ALRDY_DOWNLODED ((DWORD)0x40150021L)

//
// MessageId: AUTOUPDATE_NONE_EDGE_SERVER_AVAILABLE
//
// MessageText:
//
// None of the configured servers are available. Please verify PMLog.log for more details.%0
//
#define AUTOUPDATE_NONE_EDGE_SERVER_AVAILABLE ((DWORD)0xC0150022L)

//
// MessageId: AUTOUPDATE_EDGE_LATEST_UPDATE_ALRDY_DOWNLODED
//
// MessageText:
//
// The latest available patch for %2!s! is %1!s!. The patch (%1!s!) was downloaded previously.%0
//
#define AUTOUPDATE_EDGE_LATEST_UPDATE_ALRDY_DOWNLODED ((DWORD)0x40150023L)

 // The following are the message definitions for Vmimage
//
// MessageId: IDS_MSG_034
//
// MessageText:
//
// Restore Item %1!d!: %2!s!%0
//
#define IDS_MSG_034                      ((DWORD)0x40160001L)

//
// MessageId: IDS_MSG_038
//
// MessageText:
//
// Invalid node type. Client Agent cannot continue the job.%0
//
#define IDS_MSG_038                      ((DWORD)0xC0160002L)

//
// MessageId: IDS_MSG_328
//
// MessageText:
//
// Skip file/dir <%1!s!>. Path is too long.%0
//
#define IDS_MSG_328                      ((DWORD)0x80160003L)

//
// MessageId: IMG_11003
//
// MessageText:
//
// Source volume is empty%0
//
#define IMG_11003                        ((DWORD)0xC0160004L)

//
// MessageId: IMG_11004
//
// MessageText:
//
// Failed to locate the $FILE_NAME attribute.%0
//
#define IMG_11004                        ((DWORD)0xC0160005L)

//
// MessageId: IMG_11005
//
// MessageText:
//
// Failed to allocate memory to read a block of data. An IndexAlloc Error has occurred.%0
//
#define IMG_11005                        ((DWORD)0xC0160006L)

//
// MessageId: IMG_11006
//
// MessageText:
//
// Failed to map an index block to the correct volume block. An IndexAlloc Error has occurred. Cannot locate VCN. (VCN=[virtual cluster number])%0
//
#define IMG_11006                        ((DWORD)0xC0160007L)

//
// MessageId: IMG_11007
//
// MessageText:
//
// Invalid index allocation header signature.%0
//
#define IMG_11007                        ((DWORD)0xC0160008L)

//
// MessageId: IMG_11008
//
// MessageText:
//
// SetFilePointer (Offset=[%1!lu!, %2!lu!], File=[%3!s!], EC=[%4!d!])%0
//
#define IMG_11008                        ((DWORD)0xC0160009L)

//
// MessageId: IMG_11009
//
// MessageText:
//
// Failed to locate a file record segment in the Master File Table (MFT).%0
//
#define IMG_11009                        ((DWORD)0xC016000AL)

//
// MessageId: IMG_11010
//
// MessageText:
//
// Failed to locate the security attribute for a file record segment from the attribute list.%0
//
#define IMG_11010                        ((DWORD)0xC016000BL)

//
// MessageId: IMG_11011
//
// MessageText:
//
// Failed to move the file. (Source=[%1!s!], Target=[%2!s!], EC=[%3!d!])%0
//
#define IMG_11011                        ((DWORD)0xC016000CL)

//
// MessageId: IMG_11014
//
// MessageText:
//
// Failed to open the directory to set compression level. (Directory=[%1!s!], EC=[%2!d!])%0
//
#define IMG_11014                        ((DWORD)0xC016000DL)

//
// MessageId: IMG_11015
//
// MessageText:
//
// Failed to set compression level for the directory. (Directory=[%1!s!], EC=[%2!d!])%0
//
#define IMG_11015                        ((DWORD)0xC016000EL)

//
// MessageId: IMG_11021
//
// MessageText:
//
// Unknown file system. (File System=[%1!s!])%0
//
#define IMG_11021                        ((DWORD)0xC016000FL)

//
// MessageId: IMG_11030
//
// MessageText:
//
// DeCompressBuffer Error. (EC=[%1!d!], File=[%2!s!])%0
//
#define IMG_11030                        ((DWORD)0xC0160010L)

//
// MessageId: IMG_11041
//
// MessageText:
//
// Skipping file.(File=[%1!s!]). %2!s!%0
//
#define IMG_11041                        ((DWORD)0x80160011L)

//
// MessageId: IMG_11046
//
// MessageText:
//
// Invalid partition type for the drive.(Drive=[%1!s!])%0
//
#define IMG_11046                        ((DWORD)0xC0160012L)

//
// MessageId: IMG_11049
//
// MessageText:
//
// Failed to read the drive. (Drive=[%1!s!],EC=[%2!d!])%0
//
#define IMG_11049                        ((DWORD)0xC0160013L)

//
// MessageId: IMG_11050
//
// MessageText:
//
// GetVHDSector failed for file. (File=[%1!s!])%0
//
#define IMG_11050                        ((DWORD)0xC0160014L)

//
// MessageId: IMG_11053
//
// MessageText:
//
// Failed to write to the Image Meta Data file. (EC=[%1!d!])%0
//
#define IMG_11053                        ((DWORD)0xC0160015L)

//
// MessageId: IMG_11054
//
// MessageText:
//
// Failed to write to Dir Struct file. (EC=[%1!d!])%0
//
#define IMG_11054                        ((DWORD)0xC0160016L)

//
// MessageId: IMG_11055
//
// MessageText:
//
// Failed to write to Cluster Mapping File. (EC=[%1!d!])%0
//
#define IMG_11055                        ((DWORD)0xC0160017L)

//
// MessageId: IMG_11056
//
// MessageText:
//
// Error restoring file fragment. (EC=[%1!d!], File=[%2!s!])%0
//
#define IMG_11056                        ((DWORD)0xC0160018L)

//
// MessageId: IMG_11059
//
// MessageText:
//
// Failed to locate the reparse point for a file record segment from the attribute list.%0
//
#define IMG_11059                        ((DWORD)0xC0160019L)

//
// MessageId: IMG_11061
//
// MessageText:
//
// Failed to restore the encrypted file. (EC=[%1!d!], File for which restore failed=[%2!s!])%0
//
#define IMG_11061                        ((DWORD)0xC016001AL)

//
// MessageId: IMG_11062
//
// MessageText:
//
// Failed to restore the reparse point. (EC=[%1!d!], File for which restore failed=[%2!s!])%0
//
#define IMG_11062                        ((DWORD)0xC016001BL)

//
// MessageId: IMG_11063
//
// MessageText:
//
// Failed to restore the object id. (EC=[%1!d!], File for which the restore failed=[%2!s!])%0
//
#define IMG_11063                        ((DWORD)0xC016001CL)

//
// MessageId: IMG_11064
//
// MessageText:
//
// Failed to locate the logged utility stream for a file record segment.%0
//
#define IMG_11064                        ((DWORD)0xC016001DL)

//
// MessageId: IMG_11065
//
// MessageText:
//
// File level restore is not possible for the RAID volume. Only RAW mode backup will be available for this volume. (Volume=[%1!s!]) %0
//
#define IMG_11065                        ((DWORD)0xC016001EL)

//
// MessageId: IMG_11066
//
// MessageText:
//
// An unexpected exception error occurred in vmdkimgdll.dll%0
//
#define IMG_11066                        ((DWORD)0xC016001FL)

//
// MessageId: IMG_11068
//
// MessageText:
//
// System files were skipped. If necessary, you can use the Bare Metal Recovery (BMR) option to restore them.%0
//
#define IMG_11068                        ((DWORD)0x80160020L)

//
// MessageId: IMG_11047
//
// MessageText:
//
// Insufficient space for restore. Free Space Required %1!d! KB, Free Space Available %2!d! KB%0
//
#define IMG_11047                        ((DWORD)0xC0160021L)

//
// MessageId: IMG_11087
//
// MessageText:
//
// Cannot freeze Backup Volume: Unable to create the Preview file because no drive has sufficient free space or a Disk Inactivity Period Timeout error occurred.%0
//
#define IMG_11087                        ((DWORD)0xC0160022L)

//
// MessageId: IMG_11001
//
// MessageText:
//
// Cannot open drive.(Drive=%1!s!, Error=%2!ld!)%0
//
#define IMG_11001                        ((DWORD)0xC0160023L)

//
// MessageId: IMG_11088
//
// MessageText:
//
// Problems with the file system were discovered. Please run CHKDSK with the /F (fix) option to correct these problems and then perform a Full Backup.%0
//
#define IMG_11088                        ((DWORD)0x40160024L)

//
// MessageId: IMG_11089
//
// MessageText:
//
// The Drive [%1!s!] does not exist.%0
//
#define IMG_11089                        ((DWORD)0xC0160025L)

//
// MessageId: IMG_11057
//
// MessageText:
//
// Insufficient space for restore on Local Disk %1!s! ,Free Disk Space=%2!I64u! MB, Total Disk Space=%3!I64u! MB%0
//
#define IMG_11057                        ((DWORD)0xC0160026L)

//
// MessageId: IMG_11058
//
// MessageText:
//
// File level restore is not supported for the (Volume=[%1!s!]) because RAID/Striped volumes have more than one disk extent per disk.%0
//
#define IMG_11058                        ((DWORD)0x80160027L)

//
// MessageId: IMG_11090
//
// MessageText:
//
// Failed to write to Cluster Mapping File. Not enough free space on volume %1!s!.%0
//
#define IMG_11090                        ((DWORD)0xC0160028L)

//
// MessageId: IMG_11091
//
// MessageText:
//
// Failed to write to Cluster Mapping File. Not enough free space on volume.%0
//
#define IMG_11091                        ((DWORD)0xC0160029L)

 // The following are the message definitions for VSB
//
// MessageId: AFRES_VSB_INF_VDDK_TRANSPORT_MODE
//
// MessageText:
//
// The transport mode used is %1!s!.%0
//
#define AFRES_VSB_INF_VDDK_TRANSPORT_MODE ((DWORD)0x40170001L)

//
// MessageId: AFRES_VSB_ERR_VDDK_ERROR
//
// MessageText:
//
// A VMware VDDK error has occurred. Error message [%1!s!]. Error code [%2!d!].%0
//
#define AFRES_VSB_ERR_VDDK_ERROR         ((DWORD)0xC0170002L)

//
// MessageId: AFRES_VSB_ERR_OPEN_VMDK_FILE
//
// MessageText:
//
// Failed to open the virtual disk [%1!s!]. A VMware VDDK error has occurred. Error message [%2!s!]. Error code [%3!d!].%0
//
#define AFRES_VSB_ERR_OPEN_VMDK_FILE     ((DWORD)0x80170003L)

//
// MessageId: AFRES_VSB_ERR_WRITE_VMDK_SECTOR
//
// MessageText:
//
// Failed to write the virtual disk [%1!s!]. A VMware VDDK error has occurred. Error message [%2!s!]. Error code [%3!d!].%0
//
#define AFRES_VSB_ERR_WRITE_VMDK_SECTOR  ((DWORD)0x80170004L)

//
// MessageId: AFRES_VSB_ERR_DISCONNECT_MONITOR
//
// MessageText:
//
// Failed to connect to node [%1!s!]. Error=[%2!s!]. Error code=[%3!d!]. Verify that the server is up, running, and reachable.%0
//
#define AFRES_VSB_ERR_DISCONNECT_MONITOR ((DWORD)0xC0170005L)

//
// MessageId: AFRES_VSB_WARN_WRITE_VMDK
//
// MessageText:
//
// Failed to write the virtual disk [%1!s!]. A VMware VDDK error has occurred. Error message [%2!s!]. Error code [%3!d!]. Retry time=[%4!d!].%0
//
#define AFRES_VSB_WARN_WRITE_VMDK        ((DWORD)0x80170006L)

//
// MessageId: AFRES_VSB_INF_BEGIN_CONVERT_SEND
//
// MessageText:
//
// Begin to convert backup disk image [%1!s!].%0
//
#define AFRES_VSB_INF_BEGIN_CONVERT_SEND ((DWORD)0x40170007L)

 // The following are the message definitions for VSPhere
//
// MessageId: AFRES_VSPHERE_0000
//
// MessageText:
//
// An unknown error occurred. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0000               ((DWORD)0xC0180001L)

//
// MessageId: AFRES_VSPHERE_0001
//
// MessageText:
//
// Memory allocation failed: out of memory. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0001               ((DWORD)0xC0180002L)

//
// MessageId: AFRES_VSPHERE_0002
//
// MessageText:
//
// One of the parameters was invalid. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0002               ((DWORD)0xC0180003L)

//
// MessageId: AFRES_VSPHERE_0003
//
// MessageText:
//
// A file %1!s! was not found. Please trigger a new virtual machine recovery job.%0
//
#define AFRES_VSPHERE_0003               ((DWORD)0xC0180004L)

//
// MessageId: AFRES_VSPHERE_0004
//
// MessageText:
//
// This function cannot be performed because the handle is executing another function. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0004               ((DWORD)0xC0180005L)

//
// MessageId: AFRES_VSPHERE_0005
//
// MessageText:
//
// The operation is not supported. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0005               ((DWORD)0xC0180006L)

//
// MessageId: AFRES_VSPHERE_0006
//
// MessageText:
//
// A file access error occurred on the host or guest operating system, %1!s!. Start a new virtual machine recovery job.%0
//
#define AFRES_VSPHERE_0006               ((DWORD)0xC0180007L)

//
// MessageId: AFRES_VSPHERE_0007
//
// MessageText:
//
// An error occurred while writing a file %1!s!; the disk is full. The data was not saved. There is not enough space in the data store. To correct this problem, free disk space.%0
//
#define AFRES_VSPHERE_0007               ((DWORD)0xC0180008L)

//
// MessageId: AFRES_VSPHERE_0008
//
// MessageText:
//
// The file %1!s! is write-protected. Start a new virtual machine recovery job.%0
//
#define AFRES_VSPHERE_0008               ((DWORD)0xC0180009L)

//
// MessageId: AFRES_VSPHERE_0009
//
// MessageText:
//
// You do not have access rights to this file %1!s!. Verify that the SAN is configured properly on the proxy machine. Then start a new virtual machine recovery job.%0
//
#define AFRES_VSPHERE_0009               ((DWORD)0xC018000AL)

//
// MessageId: AFRES_VSPHERE_0010
//
// MessageText:
//
// The VMFS file system does not support sufficiently large files. The disk file is greater than 2TB.%0
//
#define AFRES_VSPHERE_0010               ((DWORD)0xC018000BL)

//
// MessageId: AFRES_VSPHERE_0011
//
// MessageText:
//
// The file %1!s! is in use by  another operation. Start a new virtual machine recovery job.%0
//
#define AFRES_VSPHERE_0011               ((DWORD)0x8018000CL)

//
// MessageId: AFRES_VSPHERE_0012
//
// MessageText:
//
// The system returned an error. Communication with the virtual machine may have been interrupted. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0012               ((DWORD)0xC018000DL)

//
// MessageId: AFRES_VSPHERE_0013
//
// MessageText:
//
// The file %1!s! is too big for the file system. Verify the maximum file size supported by the version of VMFS. The disk file is greater than 2TB.%0
//
#define AFRES_VSPHERE_0013               ((DWORD)0xC018000EL)

//
// MessageId: AFRES_VSPHERE_0014
//
// MessageText:
//
// The request refers to an object that no longer exists or  never existed. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0014               ((DWORD)0xC018000FL)

//
// MessageId: AFRES_VSPHERE_0015
//
// MessageText:
//
// Unable to connect to the host %1!s!. Verify that the vSphere proxy machine and the ESX/vCenter server are connected to network.%0
//
#define AFRES_VSPHERE_0015               ((DWORD)0xC0180010L)

//
// MessageId: AFRES_VSPHERE_0016
//
// MessageText:
//
// This operation is not supported with the current license. Verify that the license is installed.%0
//
#define AFRES_VSPHERE_0016               ((DWORD)0xC0180011L)

//
// MessageId: AFRES_VSPHERE_0017
//
// MessageText:
//
// Unable to communicate with the virtual machine's host; it appears to be  disconnected. Verify that the vSphere proxy machine and the ESX/vCenter server are connected to network.%0
//
#define AFRES_VSPHERE_0017               ((DWORD)0xC0180012L)

//
// MessageId: AFRES_VSPHERE_0018
//
// MessageText:
//
// The handle is not a valid VIX object. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0018               ((DWORD)0xC0180013L)

//
// MessageId: AFRES_VSPHERE_0019
//
// MessageText:
//
// The operation is not supported on this type of handle. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0019               ((DWORD)0xC0180014L)

//
// MessageId: AFRES_VSPHERE_0020
//
// MessageText:
//
// There are too many handles open. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0020               ((DWORD)0xC0180015L)

//
// MessageId: AFRES_VSPHERE_0021
//
// MessageText:
//
// Invalid file - a required section of the file is missing. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0021               ((DWORD)0xC0180016L)

//
// MessageId: AFRES_VSPHERE_0022
//
// MessageText:
//
// A timeout error occurred. Verify that the vSphere proxy machine and  ESX/vCenter server are connected to network.%0
//
#define AFRES_VSPHERE_0022               ((DWORD)0xC0180017L)

//
// MessageId: AFRES_VSPHERE_0023
//
// MessageText:
//
// Insufficient permissions for the operating system on host %1!s! Verify that the user name and password are correct.%0
//
#define AFRES_VSPHERE_0023               ((DWORD)0xC0180018L)

//
// MessageId: AFRES_VSPHERE_0024
//
// MessageText:
//
// The virtual machine is blocked waiting for a user operation. Verify that the virtual machine user operation pending pop up in VI Client. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0024               ((DWORD)0xC0180019L)

//
// MessageId: AFRES_VSPHERE_0025
//
// MessageText:
//
// The command is not allowed by this login type. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0025               ((DWORD)0xC018001AL)

//
// MessageId: AFRES_VSPHERE_0026
//
// MessageText:
//
// The virtual machine cannot be found. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0026               ((DWORD)0xC018001BL)

//
// MessageId: AFRES_VSPHERE_0027
//
// MessageText:
//
// The operation is not supported for this virtual machine version. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0027               ((DWORD)0xC018001CL)

//
// MessageId: AFRES_VSPHERE_0028
//
// MessageText:
//
// The virtual machine was  loaded previously. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0028               ((DWORD)0xC018001DL)

//
// MessageId: AFRES_VSPHERE_0029
//
// MessageText:
//
// Disk sector size check failed. Perform a full backup.%0
//
#define AFRES_VSPHERE_0029               ((DWORD)0xC018001EL)

//
// MessageId: AFRES_VSPHERE_0030
//
// MessageText:
//
// Error in protocol. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0030               ((DWORD)0xC018001FL)

//
// MessageId: AFRES_VSPHERE_0031
//
// MessageText:
//
// Unable to create socket. Verify that proxy and the host can communicate with each other via the network. Verify that the vSphere proxy machine and the ESX/vCenter server are connected to the network.%0
//
#define AFRES_VSPHERE_0031               ((DWORD)0xC0180020L)

//
// MessageId: AFRES_VSPHERE_0032
//
// MessageText:
//
// The specified server %1!s! cannot be contacted. Verify that the network connection and port setting of the ESX or vCenter server are configured properly.%0
//
#define AFRES_VSPHERE_0032               ((DWORD)0xC0180021L)

//
// MessageId: AFRES_VSPHERE_0033
//
// MessageText:
//
// The server %1!s! refused the connection. Verify that the network connection and port setting of the ESX or vCenter server are configured properly. Verify that the vSphere proxy machine and the ESX/vCenter server are connected to the network.%0
//
#define AFRES_VSPHERE_0033               ((DWORD)0xC0180022L)

//
// MessageId: AFRES_VSPHERE_0034
//
// MessageText:
//
// A communication error occurred. Verify that the proxy and the host can communicate with each other via the network. Verify that the vSphere proxy machine and the ESX/vCenter server are connected to the network.%0
//
#define AFRES_VSPHERE_0034               ((DWORD)0xC0180023L)

//
// MessageId: AFRES_VSPHERE_0035
//
// MessageText:
//
// The connection was lost. Verify that the ESX/vCenter server can communicate via the network. Try reconnecting. Verify that the vSphere proxy machine and the ESX/vCenter server are connected to the network.%0
//
#define AFRES_VSPHERE_0035               ((DWORD)0xC0180024L)

//
// MessageId: AFRES_VSPHERE_0036
//
// MessageText:
//
// VDDK write failed with error NBD_ERR_HASHFILE_VOLUME. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0036               ((DWORD)0xC0180025L)

//
// MessageId: AFRES_VSPHERE_0037
//
// MessageText:
//
// VDDK write failed with error NBD_ERR_HASHFILE_INIT. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0037               ((DWORD)0xC0180026L)

//
// MessageId: AFRES_VSPHERE_0038
//
// MessageText:
//
// One of the parameters supplied is invalid. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0038               ((DWORD)0xC0180027L)

//
// MessageId: AFRES_VSPHERE_0039
//
// MessageText:
//
// The disk library was not  initialized. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0039               ((DWORD)0xC0180028L)

//
// MessageId: AFRES_VSPHERE_0040
//
// MessageText:
//
// The called function requires the virtual disk to be opened for I/O. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0040               ((DWORD)0xC0180029L)

//
// MessageId: AFRES_VSPHERE_0041
//
// MessageText:
//
// The called function cannot be performed on partial chains.  Open the parent virtual disk. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0041               ((DWORD)0xC018002AL)

//
// MessageId: AFRES_VSPHERE_0042
//
// MessageText:
//
// The specified virtual disk needs repair. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0042               ((DWORD)0xC018002BL)

//
// MessageId: AFRES_VSPHERE_0043
//
// MessageText:
//
// You  requested access to an area of the virtual disk that is out of bounds. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0043               ((DWORD)0xC018002CL)

//
// MessageId: AFRES_VSPHERE_0044
//
// MessageText:
//
// The parent virtual disk was modified after  the child was created. Verify that the parent disk was not corrupted by another operation.%0
//
#define AFRES_VSPHERE_0044               ((DWORD)0xC018002DL)

//
// MessageId: AFRES_VSPHERE_0045
//
// MessageText:
//
// The specified virtual disk cannot be shrunk because it is not the parent disk. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0045               ((DWORD)0xC018002EL)

//
// MessageId: AFRES_VSPHERE_0046
//
// MessageText:
//
// The partition table on the physical disk  changed after the disk was created. Remove the physical disk from the virtual machine, and then add it again.%0
//
#define AFRES_VSPHERE_0046               ((DWORD)0x8018002FL)

//
// MessageId: AFRES_VSPHERE_0047
//
// MessageText:
//
// than the version supported by this program. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0047               ((DWORD)0xC0180030L)

//
// MessageId: AFRES_VSPHERE_0048
//
// MessageText:
//
// The parent of this virtual disk could not be opened. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0048               ((DWORD)0xC0180031L)

//
// MessageId: AFRES_VSPHERE_0049
//
// MessageText:
//
// The specified feature is not supported by this version. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0049               ((DWORD)0xC0180032L)

//
// MessageId: AFRES_VSPHERE_0050
//
// MessageText:
//
// One or more required keys were not provided. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0050               ((DWORD)0xC0180033L)

//
// MessageId: AFRES_VSPHERE_0051
//
// MessageText:
//
// An unencrypted child of the encrypted disk will not be created without an explicit request. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0051               ((DWORD)0xC0180034L)

//
// MessageId: AFRES_VSPHERE_0052
//
// MessageText:
//
// Not an encrypted disk. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0052               ((DWORD)0xC0180035L)

//
// MessageId: AFRES_VSPHERE_0053
//
// MessageText:
//
// No keys supplied for encrypting disk. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0053               ((DWORD)0xC0180036L)

//
// MessageId: AFRES_VSPHERE_0054
//
// MessageText:
//
// The partition table is invalid. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0054               ((DWORD)0xC0180037L)

//
// MessageId: AFRES_VSPHERE_0055
//
// MessageText:
//
// Only sparse extents with embedded descriptors may be encrypted. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0055               ((DWORD)0xC0180038L)

//
// MessageId: AFRES_VSPHERE_0056
//
// MessageText:
//
// Not an encrypted descriptor file. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0056               ((DWORD)0xC0180039L)

//
// MessageId: AFRES_VSPHERE_0057
//
// MessageText:
//
// The file system is not VMFS. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0057               ((DWORD)0xC018003AL)

//
// MessageId: AFRES_VSPHERE_0058
//
// MessageText:
//
// The physical disk is too big. The maximum size allowed is 2TB. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0058               ((DWORD)0xC018003BL)

//
// MessageId: AFRES_VSPHERE_0059
//
// MessageText:
//
// The  limit for open files on the host was  exceeded. The disk file is greater than 2TB.%0
//
#define AFRES_VSPHERE_0059               ((DWORD)0xC018003CL)

//
// MessageId: AFRES_VSPHERE_0060
//
// MessageText:
//
// Too many levels of redo logs. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0060               ((DWORD)0xC018003DL)

//
// MessageId: AFRES_VSPHERE_0061
//
// MessageText:
//
// The physical disk is too small. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0061               ((DWORD)0xC018003EL)

//
// MessageId: AFRES_VSPHERE_0062
//
// MessageText:
//
// The  disk chain is not valid: cannot mix hosted and managed style disks in the same chain. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0062               ((DWORD)0xC018003FL)

//
// MessageId: AFRES_VSPHERE_0063
//
// MessageText:
//
// The specified key is not found in the disk data base. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0063               ((DWORD)0xC0180040L)

//
// MessageId: AFRES_VSPHERE_0064
//
// MessageText:
//
// One or more of the required subsystems failed to initialize. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0064               ((DWORD)0xC0180041L)

//
// MessageId: AFRES_VSPHERE_0065
//
// MessageText:
//
// An invalid connection handle error occurred. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0065               ((DWORD)0xC0180042L)

//
// MessageId: AFRES_VSPHERE_0066
//
// MessageText:
//
// A disk encoding error occurred. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0066               ((DWORD)0xC0180043L)

//
// MessageId: AFRES_VSPHERE_0067
//
// MessageText:
//
// The disk is corrupted and unrepairable. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0067               ((DWORD)0xC0180044L)

//
// MessageId: AFRES_VSPHERE_0068
//
// MessageText:
//
// The specified file is not a virtual disk. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0068               ((DWORD)0xC0180045L)

//
// MessageId: AFRES_VSPHERE_0069
//
// MessageText:
//
// The host is not licensed for this feature.  Contact arcserve support.%0
//
#define AFRES_VSPHERE_0069               ((DWORD)0xC0180046L)

//
// MessageId: AFRES_VSPHERE_0070
//
// MessageText:
//
// The device does not exist.  Contact arcserve support.%0
//
#define AFRES_VSPHERE_0070               ((DWORD)0xC0180047L)

//
// MessageId: AFRES_VSPHERE_0071
//
// MessageText:
//
// The operation is not supported on this type of device. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0071               ((DWORD)0xC0180048L)

//
// MessageId: AFRES_VSPHERE_0072
//
// MessageText:
//
// Cannot connect to host. Verify that the proxy and the ESX/vCenter server can communicate with each other via the network.  Verify that the vSphere proxy machine and the ESX/vCenter server are connected to the network.%0
//
#define AFRES_VSPHERE_0072               ((DWORD)0xC0180049L)

//
// MessageId: AFRES_VSPHERE_0073
//
// MessageText:
//
// File %1!s! name too long. Contact arcserve support.%0
//
#define AFRES_VSPHERE_0073               ((DWORD)0xC018004AL)

//
// MessageId: AFRES_VSPHERE_0074_VMDK_IO_CONN
//
// MessageText:
//
// The VMDK IO connection failed. VMware reported the following error: %1!s!. For more information, see the debug log %2!s!. If necessary, contact arcserve support.%0
//
#define AFRES_VSPHERE_0074_VMDK_IO_CONN  ((DWORD)0xC018004BL)

//
// MessageId: AFRES_VSPHERE_0075_OPENVMDKFILE
//
// MessageText:
//
// Unable to open VMDK file %1!s!. VMware reported the following error: %2!s!. For more information, see the debug log %3!s!. If necessary, contact arcserve support.%0
//
#define AFRES_VSPHERE_0075_OPENVMDKFILE  ((DWORD)0xC018004CL)

//
// MessageId: AFRES_VSPHERE_0076_GETVMDKSECTORS
//
// MessageText:
//
// Unable to read from VMDK file %1!s!. VMware reported the following error: %2!s!. For more information, see the debug log %3!s!. If necessary, contact arcserve support.%0
//
#define AFRES_VSPHERE_0076_GETVMDKSECTORS ((DWORD)0xC018004DL)

//
// MessageId: AFRES_VSPHERE_0077
//
// MessageText:
//
// VM recovery job was unable to read the D2D backup session. The session may be inaccessible or corrupt. To  correct the problem, submit a new backup job using the new backup session. If the problem persists, contact arcserve support.%0
//
#define AFRES_VSPHERE_0077               ((DWORD)0xC018004EL)

//
// MessageId: AFRES_VSPHERE_0078
//
// MessageText:
//
// An error occurred while reading source session. For more information, see the VM recovery logs located in %1!s!\Logs. Additionally, see Troubleshooting in the User Guide.%0
//
#define AFRES_VSPHERE_0078               ((DWORD)0xC018004FL)

//
// MessageId: AFRES_VSPHERE_0079
//
// MessageText:
//
// Unable to create directory %1!s! in the guest operating system on the virtual machine. VMware VIX reports error: %2!s!.%0
//
#define AFRES_VSPHERE_0079               ((DWORD)0xC0180050L)

//
// MessageId: AFRES_VSPHERE_0080
//
// MessageText:
//
// Unable to delete directory %1!s! in the guest operating system on the virtual machine. VMware VIX reports error: %2!s!.%0
//
#define AFRES_VSPHERE_0080               ((DWORD)0xC0180051L)

//
// MessageId: AFRES_VSPHERE_0081
//
// MessageText:
//
// Changed block tracking was enabled with virtual machine snapshots present. The full backups will now include the used and unused blocks of data contained in the VMDK files of the virtual machine.%0
//
#define AFRES_VSPHERE_0081               ((DWORD)0x80180052L)

//
// MessageId: AFRES_VSPHERE_0082
//
// MessageText:
//
// The application will backup all the blocks for VMDK %1!s! because VMware cannot retrieve the used blocks if there are snapshots when enabling changed block tracking on the virtual machine.%0
//
#define AFRES_VSPHERE_0082               ((DWORD)0xC0180053L)

//
// MessageId: AFRES_VSPHERE_0083
//
// MessageText:
//
// Changed block tracking was reset with virtual machine snapshots present. The full backups will now include the used and unused blocks of data contained in the VMDK files of the virtual machine.%0
//
#define AFRES_VSPHERE_0083               ((DWORD)0x40180054L)

//
// MessageId: AFRES_VSPHERE_0084
//
// MessageText:
//
// The VMDK files of the virtual machine were opened for backup. Migration using Storage vMotion cannot be performed until the backup completes or is canceled.%0
//
#define AFRES_VSPHERE_0084               ((DWORD)0x40180055L)

//
// MessageId: AFRES_VSPHERE_0085
//
// MessageText:
//
// The VMDK files of the virtual machine cannot be opened. The VMDK files may be migrating using Storage vMotion or a maintenance task is in progress. The backup of the virtual machine cannot continue until the current operation completes.%0
//
#define AFRES_VSPHERE_0085               ((DWORD)0xC0180056L)

//
// MessageId: AFRES_VSPHERE_0086
//
// MessageText:
//
// The VMDK files of the virtual machine were closed.%0
//
#define AFRES_VSPHERE_0086               ((DWORD)0x40180057L)

//
// MessageId: AFRES_VSPHERE_0087
//
// MessageText:
//
// VMware cannot retrieve the used blocks of data for full backups when changed block tracking was reset while virtual machine snapshots were present.%0
//
#define AFRES_VSPHERE_0087               ((DWORD)0xC0180058L)

//
// MessageId: AFRES_VSPHERE_0088
//
// MessageText:
//
// VMware cannot retrieve the used blocks of data for full backups when changed block tracking was enabled while virtual machine snapshots were present.%0
//
#define AFRES_VSPHERE_0088               ((DWORD)0x80180059L)

//
// MessageId: AFRES_VSPHERE_0089
//
// MessageText:
//
// Backing up virtual machine %1!s! on %2!s!. <VMInstance UUID=%3!s!>%0
//
#define AFRES_VSPHERE_0089               ((DWORD)0x4018005AL)

//
// MessageId: AFRES_VSPHERE_0090
//
// MessageText:
//
// VMware does not support application-level quiescing of Windows 2008 and later virtual machines with dynamic disks that are running on ESX server 4.1 or later.%0
//
#define AFRES_VSPHERE_0090               ((DWORD)0xC018005BL)

//
// MessageId: AFRES_VSPHERE_0091
//
// MessageText:
//
// ESX server %1!s! is in maintenance mode. Try backup later after maintenance completes.%0
//
#define AFRES_VSPHERE_0091               ((DWORD)0xC018005CL)

//
// MessageId: AFRES_VSPHERE_0092
//
// MessageText:
//
// The user-defined transport mode %1!s! is not available. The backup job will use the best available transport mode.%0
//
#define AFRES_VSPHERE_0092               ((DWORD)0xC018005DL)

//
// MessageId: AFRES_VSPHERE_0093
//
// MessageText:
//
// The snapshot of the virtual machine cannot be deleted at this time; it will be deleted the next time a backup job runs.%0
//
#define AFRES_VSPHERE_0093               ((DWORD)0xC018005EL)

//
// MessageId: AFRES_VSPHERE_0094
//
// MessageText:
//
// The VMDK IO cleanup operation failed. VMware reported the following error: %1!s!. For more information, see the debug log %2!s!. If necessary, contact arcserve support.%0
//
#define AFRES_VSPHERE_0094               ((DWORD)0xC018005FL)

//
// MessageId: AFRES_VSPHERE_0095
//
// MessageText:
//
// An unknown error has occurred. See the VDDK debug log file: %1!s!.%0
//
#define AFRES_VSPHERE_0095               ((DWORD)0xC0180060L)

//
// MessageId: AFRES_VSPHERE_0075_OPENVMDKFILE_NET
//
// MessageText:
//
// Unable to open VMDK file %1!s!. VMware reported the following error: %2!s!. It can also be caused by network connection problems to ESX(i) server. For more information, see the debug log %3!s!. If necessary, contact arcserve support.%0
//
#define AFRES_VSPHERE_0075_OPENVMDKFILE_NET ((DWORD)0xC0180061L)

 // The following are the message definitions for VSS
//
// MessageId: AFRES_AFVSWP_APPLICATION_RESTORE_BEGIN
//
// MessageText:
//
// Application restore begins...%0
//
#define AFRES_AFVSWP_APPLICATION_RESTORE_BEGIN ((DWORD)0x40190001L)

//
// MessageId: AFRES_AFVSWP_APPLICATION_RESTORE_SUCCEED
//
// MessageText:
//
// Application successfully restored!%0
//
#define AFRES_AFVSWP_APPLICATION_RESTORE_SUCCEED ((DWORD)0x40190002L)

//
// MessageId: AFRES_AFVSWP_APPLICATION_RESTORE_FAILED
//
// MessageText:
//
// Application restore failed. (EC=[%1!d!])%0
//
#define AFRES_AFVSWP_APPLICATION_RESTORE_FAILED ((DWORD)0xC0190003L)

//
// MessageId: AFRES_AFVSWP_APPLICATION_RESTORE_END
//
// MessageText:
//
// Application restore finished.%0
//
#define AFRES_AFVSWP_APPLICATION_RESTORE_END ((DWORD)0x40190004L)

//
// MessageId: AFRES_AFVSWP_STOP_SERVICE
//
// MessageText:
//
// Begin to stop service. (Service Name=[%1!s!])%0
//
#define AFRES_AFVSWP_STOP_SERVICE        ((DWORD)0x40190005L)

//
// MessageId: AFRES_AFVSWP_STOP_SERVICE_SUCCEED
//
// MessageText:
//
// Service successfully stopped! (Service Name=[%1!s!])%0
//
#define AFRES_AFVSWP_STOP_SERVICE_SUCCEED ((DWORD)0x40190006L)

//
// MessageId: AFRES_AFVSWP_STOP_SERVICE_FAILED
//
// MessageText:
//
// Failed to stop service. (EC=[%1!d!], Service Name=[%2!s!])%0
//
#define AFRES_AFVSWP_STOP_SERVICE_FAILED ((DWORD)0xC0190007L)

//
// MessageId: AFRES_AFVSWP_START_SERVICE
//
// MessageText:
//
// Begin to start service. (Service name=[%1!s!])%0
//
#define AFRES_AFVSWP_START_SERVICE       ((DWORD)0x40190008L)

//
// MessageId: AFRES_AFVSWP_START_SERVICE_SUCCEED
//
// MessageText:
//
// Service successfully started! (Service name=[%1!s!])%0
//
#define AFRES_AFVSWP_START_SERVICE_SUCCEED ((DWORD)0x40190009L)

//
// MessageId: AFRES_AFVSWP_START_SERVICE_FAILED
//
// MessageText:
//
// Failed to start service. (EC=[%1!d!], Service Name=[%2!s!])%0
//
#define AFRES_AFVSWP_START_SERVICE_FAILED ((DWORD)0x8019000AL)

//
// MessageId: AFRES_AFVSWP_DISMOUNT_DB
//
// MessageText:
//
// Begin to dismount database. (Database Name=[%1!s!])%0
//
#define AFRES_AFVSWP_DISMOUNT_DB         ((DWORD)0x4019000BL)

//
// MessageId: AFRES_AFVSWP_DISMOUNT_DB_SUCCEED
//
// MessageText:
//
// Database successfully dismounted! (Database Name=[%1!s!])%0
//
#define AFRES_AFVSWP_DISMOUNT_DB_SUCCEED ((DWORD)0x4019000CL)

//
// MessageId: AFRES_AFVSWP_DISMOUNT_DB_FAILED
//
// MessageText:
//
// Failed to dismount database. (EC=[%1!d!], Database Name=[%2!s!])%0
//
#define AFRES_AFVSWP_DISMOUNT_DB_FAILED  ((DWORD)0xC019000DL)

//
// MessageId: AFRES_AFVSWP_MOUNT_DB
//
// MessageText:
//
// Begin to mount database. (Database Name=[%1!s!])%0
//
#define AFRES_AFVSWP_MOUNT_DB            ((DWORD)0x4019000EL)

//
// MessageId: AFRES_AFVSWP_MOUNT_DB_SUCCEED
//
// MessageText:
//
// Database successfully mounted! (Database Name=[%1!s!])%0
//
#define AFRES_AFVSWP_MOUNT_DB_SUCCEED    ((DWORD)0x4019000FL)

//
// MessageId: AFRES_AFVSWP_MOUNT_DB_FAILED
//
// MessageText:
//
// Failed to mount database. (EC=[%1!d!], Database Name=[%2!s!])%0
//
#define AFRES_AFVSWP_MOUNT_DB_FAILED     ((DWORD)0xC0190010L)

//
// MessageId: AFRES_AFVSWP_WRITER2BRESTORED
//
// MessageText:
//
// Select writer(Writer Name=[%1!s!], Writer ID=[%2!s!], Writer Instance Name=[%3!s!]) to be restored...%0
//
#define AFRES_AFVSWP_WRITER2BRESTORED    ((DWORD)0x40190011L)

//
// MessageId: AFRES_AFVSWP_WRITER_NOT_RUNNING
//
// MessageText:
//
// Writer(Name=[%1!s!], ID=[%2!s!]) does not exist or is not running.%0
//
#define AFRES_AFVSWP_WRITER_NOT_RUNNING  ((DWORD)0xC0190012L)

//
// MessageId: AFRES_AFVSWP_COMPONENT_NOT_FOUND
//
// MessageText:
//
// Component(Logical Path=[%1!s!], Component Name=[%2!s!]) cannot be found.%0
//
#define AFRES_AFVSWP_COMPONENT_NOT_FOUND ((DWORD)0x80190013L)

//
// MessageId: AFRES_AFVSWP_EXCULDED_NO_COMPONENT_INCLUDED
//
// MessageText:
//
// Writer(Name=[%1!s!], ID=[%2!s!]) will be excluded because no component is selected.%0
//
#define AFRES_AFVSWP_EXCULDED_NO_COMPONENT_INCLUDED ((DWORD)0x80190014L)

//
// MessageId: AFRES_AFVSWP_RESTORE_MASTER_DB
//
// MessageText:
//
// Begin to restore master database. (Instance Name=[%1!s!])%0
//
#define AFRES_AFVSWP_RESTORE_MASTER_DB   ((DWORD)0x40190015L)

//
// MessageId: AFRES_AFVSWP_RESTORE_MASTER_DB_SUCCEED
//
// MessageText:
//
// Master database successfully restored! (Instance Name=[%1!s!])%0
//
#define AFRES_AFVSWP_RESTORE_MASTER_DB_SUCCEED ((DWORD)0x40190016L)

//
// MessageId: AFRES_AFVSWP_RESTORE_MASTER_DB_FAILED
//
// MessageText:
//
// Failed to restore master database! (EC=[%1!d!], Instance Name=[%2!s!])%0
//
#define AFRES_AFVSWP_RESTORE_MASTER_DB_FAILED ((DWORD)0xC0190017L)

//
// MessageId: AFRES_AFVSWP_RESTORE_TO_ORIGINAL_LOCATION
//
// MessageText:
//
// Restore to original location.%0
//
#define AFRES_AFVSWP_RESTORE_TO_ORIGINAL_LOCATION ((DWORD)0x40190018L)

//
// MessageId: AFRES_AFVSWP_RESTORE_TO_ALTERNATE_LOCATION
//
// MessageText:
//
// Restore to alternate location.%0
//
#define AFRES_AFVSWP_RESTORE_TO_ALTERNATE_LOCATION ((DWORD)0x40190019L)

//
// MessageId: AFRES_AFVSWP_PRERESTORE_STAGE
//
// MessageText:
//
// Pre-Restore stage...%0
//
#define AFRES_AFVSWP_PRERESTORE_STAGE    ((DWORD)0x4019001AL)

//
// MessageId: AFRES_AFVSWP_POSTRESTORE_STAGE
//
// MessageText:
//
// Post-Restore stage...%0
//
#define AFRES_AFVSWP_POSTRESTORE_STAGE   ((DWORD)0x4019001BL)

//
// MessageId: AFRES_AFVSWP_RESTORE_SELECTED_FILE_SUCCEED
//
// MessageText:
//
// Restoring selected files succeeds!%0
//
#define AFRES_AFVSWP_RESTORE_SELECTED_FILE_SUCCEED ((DWORD)0x4019001CL)

//
// MessageId: AFRES_AFVSWP_RESTORE_SELECTED_FILE_FAILED
//
// MessageText:
//
// Restore of the selected files is incomplete.%0
//
#define AFRES_AFVSWP_RESTORE_SELECTED_FILE_FAILED ((DWORD)0x8019001DL)

//
// MessageId: AFRES_AFVSWP_WRITER_WITH_BAD_STATUS
//
// MessageText:
//
// Writer(Name=[%1!s!], ID=[%2!s!]) has a bad status, please refer to Windows Event log for more details. (Status=[0x%3!08x!])%0
//
#define AFRES_AFVSWP_WRITER_WITH_BAD_STATUS ((DWORD)0xC019001EL)

//
// MessageId: AFRES_AFVSWP_LIST_NO_WRITER
//
// MessageText:
//
// Cannot find any application writer running in system. Start writer service or re-register COM components may help resolve this problem.%0
//
#define AFRES_AFVSWP_LIST_NO_WRITER      ((DWORD)0xC019001FL)

//
// MessageId: AFRES_AFVSWP_SNAPSHOT_FOR_VOLUME
//
// MessageText:
//
// Including volume %1!s! in snapshot.%0
//
#define AFRES_AFVSWP_SNAPSHOT_FOR_VOLUME ((DWORD)0x40190020L)

//
// MessageId: AFRES_AFVSWP_SNAPSHOT_SET_IN_PROGRESS
//
// MessageText:
//
// The creation of snapshot is in progress, and only one snapshot creation operation can be in progress at one time. retry after %1!u! seconds%0
//
#define AFRES_AFVSWP_SNAPSHOT_SET_IN_PROGRESS ((DWORD)0x80190021L)

//
// MessageId: AFRES_AFVSWP_START_SNAPSHOT_SET_SUCCEED
//
// MessageText:
//
// Start snapshot set. (Snapshot Set ID=[%1!s!])%0
//
#define AFRES_AFVSWP_START_SNAPSHOT_SET_SUCCEED ((DWORD)0x40190022L)

//
// MessageId: AFRES_AFVSWP_START_SNAPSHOT_SET_FAILED
//
// MessageText:
//
// Failed to start snapshot set.%0
//
#define AFRES_AFVSWP_START_SNAPSHOT_SET_FAILED ((DWORD)0xC0190023L)

//
// MessageId: AFRES_AFVSWP_ADD_VOLUMES_TO_SNAPSHOT_SET
//
// MessageText:
//
// Volume %1!s! has been added into snapshot set, (Snapshot ID=[%2!s!])%0
//
#define AFRES_AFVSWP_ADD_VOLUMES_TO_SNAPSHOT_SET ((DWORD)0x40190024L)

//
// MessageId: AFRES_AFVSWP_LAST_ASYNCH_OPERATION_UNEXPECTED_PROVIDER_ERROR
//
// MessageText:
//
// The provider returned an unexpected error code. This can be a transient problem. Retry after %1!u! seconds.%0
//
#define AFRES_AFVSWP_LAST_ASYNCH_OPERATION_UNEXPECTED_PROVIDER_ERROR ((DWORD)0x80190025L)

//
// MessageId: AFRES_AFVSWP_LAST_ASYNCH_OPERATION_PROVIDER_VETO
//
// MessageText:
//
// The provider was unable to perform the request at this time. This can be a transient problem. Retry after %1!u! seconds.%0
//
#define AFRES_AFVSWP_LAST_ASYNCH_OPERATION_PROVIDER_VETO ((DWORD)0x80190026L)

//
// MessageId: AFRES_AFVSWP_LAST_ASYNCH_OPERATION_UNKNOWN_ERROR
//
// MessageText:
//
// Error during the last asynchronous operation, please refer to Windows Event log for more details. (EC=[0x%1!08x!], MSG=[%2!s!])%0
//
#define AFRES_AFVSWP_LAST_ASYNCH_OPERATION_UNKNOWN_ERROR ((DWORD)0xC0190027L)

//
// MessageId: AFRES_AFVSWP_PREPARE_FOR_BACKUP_STAGE
//
// MessageText:
//
// Prepare for backup stage...%0
//
#define AFRES_AFVSWP_PREPARE_FOR_BACKUP_STAGE ((DWORD)0x40190028L)

//
// MessageId: AFRES_AFVSWP_PREPARE_FOR_BACKUP_FAILED
//
// MessageText:
//
// Failed to prepare for backup.%0
//
#define AFRES_AFVSWP_PREPARE_FOR_BACKUP_FAILED ((DWORD)0xC0190029L)

//
// MessageId: AFRES_AFVSWP_CREATE_SNAPSHOT_STAGE
//
// MessageText:
//
// Create snapshots for selected volumes...%0
//
#define AFRES_AFVSWP_CREATE_SNAPSHOT_STAGE ((DWORD)0x4019002AL)

//
// MessageId: AFRES_AFVSWP_CREATE_SNAPSHOT_FAILED
//
// MessageText:
//
// Failed to create snapshot for selected volumes.%0
//
#define AFRES_AFVSWP_CREATE_SNAPSHOT_FAILED ((DWORD)0xC019002BL)

//
// MessageId: AFRES_AFVSWP_SAVE_WRITER_METADATA_SUCCEED
//
// MessageText:
//
// Save writer metadata. (Metadata Name=[%1!s!], Size=[%2!u!])%0
//
#define AFRES_AFVSWP_SAVE_WRITER_METADATA_SUCCEED ((DWORD)0x4019002CL)

//
// MessageId: AFRES_AFVSWP_SAVE_WRITER_METADATA_FAILED
//
// MessageText:
//
// Failed to save metadata. (Writer Name=[%1!s!])%0
//
#define AFRES_AFVSWP_SAVE_WRITER_METADATA_FAILED ((DWORD)0xC019002DL)

//
// MessageId: AFRES_AFVSWP_SQL_WRITER_IS_NOT_INSTALLED
//
// MessageText:
//
// SQL writer service is not installed, SQL server will be skipped to back up. If you want to back SQL server up, please install SQL writer and make sure it is running.%0
//
#define AFRES_AFVSWP_SQL_WRITER_IS_NOT_INSTALLED ((DWORD)0x8019002EL)

//
// MessageId: AFRES_AFVSWP_SQL_WRITER_IS_NOT_RUNNING
//
// MessageText:
//
// SQL writer service will be started automatically to back up SQL server.%0
//
#define AFRES_AFVSWP_SQL_WRITER_IS_NOT_RUNNING ((DWORD)0x8019002FL)

//
// MessageId: AFRES_AFVSWP_RDB_NOT_FOUND
//
// MessageText:
//
// Unable to find recovery database. (Name=[%1!s!])%0
//
#define AFRES_AFVSWP_RDB_NOT_FOUND       ((DWORD)0xC0190030L)

//
// MessageId: AFRES_AFVSWP_STOP_SERVICE_TO_RESTORE_MASTER
//
// MessageText:
//
// Master database should be restored after SQL server relevant services have been stopped.%0
//
#define AFRES_AFVSWP_STOP_SERVICE_TO_RESTORE_MASTER ((DWORD)0x40190031L)

//
// MessageId: AFRES_AFVSWP_APPPLUGIN_NOT_LICENSED
//
// MessageText:
//
// Application restore option is not licensed. (Option Name=[%1!s!])%0
//
#define AFRES_AFVSWP_APPPLUGIN_NOT_LICENSED ((DWORD)0xC0190032L)

//
// MessageId: AFRES_AFVSWP_WRITER_NOT_LICENSED_SKIPPED_TO_BACKUP
//
// MessageText:
//
// Writer has been skipped from this backup job because the application protection feature is not licensed.(Writer Name=[%1!s!])%0
//
#define AFRES_AFVSWP_WRITER_NOT_LICENSED_SKIPPED_TO_BACKUP ((DWORD)0x80190033L)

//
// MessageId: AFRES_AFVSWP_WRITER_NOT_LICENSED_CANNOT_RESTORE
//
// MessageText:
//
// Writer cannot be restored because application protection feature is not licensed. (Writer Name=[%1!s!])%0
//
#define AFRES_AFVSWP_WRITER_NOT_LICENSED_CANNOT_RESTORE ((DWORD)0xC0190034L)

//
// MessageId: AFRES_AFVSWP_NO_LICENSE_INFO_APP_CANNOT_BACKUP
//
// MessageText:
//
// Application backup will be skipped because application protection feature is not licensed.%0
//
#define AFRES_AFVSWP_NO_LICENSE_INFO_APP_CANNOT_BACKUP ((DWORD)0x80190035L)

//
// MessageId: AFRES_AFVSWP_NO_LICENSE_INFO_APP_CANNOT_RESTORE
//
// MessageText:
//
// Application restore cannot be performed because application protection feature is not licensed.%0
//
#define AFRES_AFVSWP_NO_LICENSE_INFO_APP_CANNOT_RESTORE ((DWORD)0xC0190036L)

//
// MessageId: AFRES_AFVSWP_APPPLUGIN_LICENSED
//
// MessageText:
//
// Application restore option has been licensed. (Option Name=[%1!s!])%0
//
#define AFRES_AFVSWP_APPPLUGIN_LICENSED  ((DWORD)0x40190037L)

//
// MessageId: AFRES_AFVSWP_WRITER_LICENCE_CHECK_PASSED
//
// MessageText:
//
// Licence check for application restore option passed. (Option Name=[%1!s!])%0
//
#define AFRES_AFVSWP_WRITER_LICENCE_CHECK_PASSED ((DWORD)0x40190038L)

//
// MessageId: AFRES_AFVSWP_COMP_SKIPPED_BACKUP_NOT_VOLUMED
//
// MessageText:
//
// VSS Component [%1!s!] is not included in backup because related volume [%2!s!] is not part of this backup.%0
//
#define AFRES_AFVSWP_COMP_SKIPPED_BACKUP_NOT_VOLUMED ((DWORD)0x80190039L)

//
// MessageId: AFRES_AFVSWP_SQLINSTANCE_NOT_EXIST
//
// MessageText:
//
// Skipping to restore database because target SQL server instance does not exist. (Database Name=[%1!s!], Instance Name=[%2!s!])%0
//
#define AFRES_AFVSWP_SQLINSTANCE_NOT_EXIST ((DWORD)0xC019003AL)

//
// MessageId: AFRES_AFVSWP_UNABLE_QUERY_ACTIVE_DIRECTORY
//
// MessageText:
//
// Unable to query information from Active Directory, Username=%1!s!%0
//
#define AFRES_AFVSWP_UNABLE_QUERY_ACTIVE_DIRECTORY ((DWORD)0xC019003BL)

//
// MessageId: AFRES_AFVSWP_RESTORE_TO_RDB
//
// MessageText:
//
// The mailbox database "%1!s!" is being restored to recovery database "%2!s!"%0
//
#define AFRES_AFVSWP_RESTORE_TO_RDB      ((DWORD)0x4019003CL)

//
// MessageId: AFRES_AFVSWP_UNABLE_TO_RESTORE_EXCH_TO_NON_ACTIVE_NODE
//
// MessageText:
//
// This Exchange restore operation can only be performed on an active node%0
//
#define AFRES_AFVSWP_UNABLE_TO_RESTORE_EXCH_TO_NON_ACTIVE_NODE ((DWORD)0xC019003DL)

//
// MessageId: AFRES_AFVSWP_NO_ENOUGH_FREE_SPACE_FOR_SNAPSHOT
//
// MessageText:
//
// Snapshot storage area for volume does not have enough free space. The snapshot storage area will be switched to another volume to continue the backup. (Storage Area=[%1!s!], Volume Name=[%2!s!])%0
//
#define AFRES_AFVSWP_NO_ENOUGH_FREE_SPACE_FOR_SNAPSHOT ((DWORD)0x8019003EL)

//
// MessageId: AFRES_AFVSWP_RESOPT_DUMP_FILES
//
// MessageText:
//
// Restore option: Dump Files.%0
//
#define AFRES_AFVSWP_RESOPT_DUMP_FILES   ((DWORD)0x4019003FL)

//
// MessageId: AFRES_AFVSWP_RESOPT_DUMP_FILES_AND_REPLAY_LOGS
//
// MessageText:
//
// Restore option: Dump Files (Replay database logs).%0
//
#define AFRES_AFVSWP_RESOPT_DUMP_FILES_AND_REPLAY_LOGS ((DWORD)0x40190040L)

//
// MessageId: AFRES_AFVSWP_RESOPT_TO_ORIGINAL_LOCATION
//
// MessageText:
//
// Restore option: Restore to original location.%0
//
#define AFRES_AFVSWP_RESOPT_TO_ORIGINAL_LOCATION ((DWORD)0x40190041L)

//
// MessageId: AFRES_AFVSWP_RESOPT_TO_ALTERNATIVE_LOCATION
//
// MessageText:
//
// Restore option: Restore to alternate location.%0
//
#define AFRES_AFVSWP_RESOPT_TO_ALTERNATIVE_LOCATION ((DWORD)0x40190042L)

//
// MessageId: AFRES_AFVSWP_RESOPT_TO_RSG_RDB
//
// MessageText:
//
// Restore option: restore exchange to RSG/RDB.%0
//
#define AFRES_AFVSWP_RESOPT_TO_RSG_RDB   ((DWORD)0x40190043L)

//
// MessageId: AFRES_AFVSWP_RESOPT_DISMOUNT_MOUNT_ENABLED
//
// MessageText:
//
// Dismount/Mount database option is selected.%0
//
#define AFRES_AFVSWP_RESOPT_DISMOUNT_MOUNT_ENABLED ((DWORD)0x40190044L)

//
// MessageId: AFRES_AFVSWP_RESOPT_DISMOUNT_MOUNT_DISABLED
//
// MessageText:
//
// Dismount/Mount database option is not selected.%0
//
#define AFRES_AFVSWP_RESOPT_DISMOUNT_MOUNT_DISABLED ((DWORD)0x40190045L)

//
// MessageId: AFRES_AFVSWP_RESOPT_DUMP_SQL_DATABASE
//
// MessageText:
//
// Restore SQL database %1!s!\%2!s!, Destination=%3!s!.%0
//
#define AFRES_AFVSWP_RESOPT_DUMP_SQL_DATABASE ((DWORD)0x40190046L)

//
// MessageId: AFRES_AFVSWP_RESOPT_RESTORE_SQL_DATABASE
//
// MessageText:
//
// Restore SQL database %1!s!\%2!s! to original location.%0
//
#define AFRES_AFVSWP_RESOPT_RESTORE_SQL_DATABASE ((DWORD)0x40190047L)

//
// MessageId: AFRES_AFVSWP_RESOPT_RESTORE_SQL_DATABASE_ALT
//
// MessageText:
//
// Restore SQL database %1!s!\%2!s!, New Database Name=%3!s!, Destination=%4!s!.%0
//
#define AFRES_AFVSWP_RESOPT_RESTORE_SQL_DATABASE_ALT ((DWORD)0x40190048L)

//
// MessageId: AFRES_AFVSWP_RESOPT_DUMP_EXCH_DATABASE
//
// MessageText:
//
// Restore Exchange storage group/database %1!s!, Destination=%2!s!.%0
//
#define AFRES_AFVSWP_RESOPT_DUMP_EXCH_DATABASE ((DWORD)0x40190049L)

//
// MessageId: AFRES_AFVSWP_RESOPT_RESTORE_EXCH_DATABASE
//
// MessageText:
//
// Restore Exchange storage group/database %1!s! to original location.%0
//
#define AFRES_AFVSWP_RESOPT_RESTORE_EXCH_DATABASE ((DWORD)0x4019004AL)

//
// MessageId: AFRES_AFVSWP_RESTORE_TO_RSG
//
// MessageText:
//
// The storage group/mailbox database "%1!s!" is being restored to the recovery storage group.%0
//
#define AFRES_AFVSWP_RESTORE_TO_RSG      ((DWORD)0x4019004BL)

//
// MessageId: AFRES_AFVSWP_INSUFFICIENT_STORAGE
//
// MessageText:
//
// The system or provider has insufficient storage space. Try again after deleting any old or unnecessary persistent shadow copies or adjusting the snapshot storage area.%0
//
#define AFRES_AFVSWP_INSUFFICIENT_STORAGE ((DWORD)0xC019004CL)

//
// MessageId: AFRES_AFVSWP_UNABLE_HOLD_IO_WRITES
//
// MessageText:
//
// The system was unable to hold I/O writes. This may be caused by a transient problem. Please retry after %1!u! seconds.%0
//
#define AFRES_AFVSWP_UNABLE_HOLD_IO_WRITES ((DWORD)0x8019004DL)

//
// MessageId: AFRES_AFVSWP_TRANSACTION_FREEZE_TIMEOUT
//
// MessageText:
//
// The system failed to freeze the Distributed Transaction Coordinator (DTC) or the Kernel Transaction Manager (KTM). Please retry after %1!u! seconds.%0
//
#define AFRES_AFVSWP_TRANSACTION_FREEZE_TIMEOUT ((DWORD)0xC019004EL)

//
// MessageId: AFRES_AFVSWP_TRANSACTION_THAW_TIMEOUT
//
// MessageText:
//
// The system failed to thaw the Distributed Transaction Coordinator (DTC) or the Kernel Transaction Manager (KTM). Please retry after %1!u! seconds.%0
//
#define AFRES_AFVSWP_TRANSACTION_THAW_TIMEOUT ((DWORD)0xC019004FL)

//
// MessageId: AFRES_AFVSWP_REBOOT_REQUIRED
//
// MessageText:
//
// The provider encountered an error that requires you to restart the computer.%0
//
#define AFRES_AFVSWP_REBOOT_REQUIRED     ((DWORD)0xC0190050L)

//
// MessageId: AFRES_AFVSWP_OUTOFMEMORY
//
// MessageText:
//
// The caller is out of memory or other system resources.%0
//
#define AFRES_AFVSWP_OUTOFMEMORY         ((DWORD)0xC0190051L)

//
// MessageId: AFRES_AFVSWP_SQL_WRITER_IS_REQUIRED_FOR_SQL_BACKUP
//
// MessageText:
//
// The service "%1!s!" must be started in order to complete SQL backup.%0
//
#define AFRES_AFVSWP_SQL_WRITER_IS_REQUIRED_FOR_SQL_BACKUP ((DWORD)0x80190052L)

//
// MessageId: AFRES_AFVSWP_RESTORE_SQL_MASTERDB
//
// MessageText:
//
// Begin to restore master database.%0
//
#define AFRES_AFVSWP_RESTORE_SQL_MASTERDB ((DWORD)0x40190053L)

//
// MessageId: AFRES_AFVSWP_RESTORE_SQL_SYSTEMDB
//
// MessageText:
//
// Begin to restore system databases.%0
//
#define AFRES_AFVSWP_RESTORE_SQL_SYSTEMDB ((DWORD)0x40190054L)

//
// MessageId: AFRES_AFVSWP_RESTORE_SQL_USERDB
//
// MessageText:
//
// Begin to restore user databases.%0
//
#define AFRES_AFVSWP_RESTORE_SQL_USERDB  ((DWORD)0x40190055L)

//
// MessageId: AFRES_AFVSWP_RESTORE_NODB_NEED
//
// MessageText:
//
// There is no database needed to be restored.%0
//
#define AFRES_AFVSWP_RESTORE_NODB_NEED   ((DWORD)0x40190056L)

//
// MessageId: AFRES_AFVSWP_RESTORE_SELECTED_SQLDB
//
// MessageText:
//
// Select SQL database to restore. (Database Name=[%1!s!])%0
//
#define AFRES_AFVSWP_RESTORE_SELECTED_SQLDB ((DWORD)0x40190057L)

//
// MessageId: AFRES_AFVSWP_EXCHANGE_WRITER_IS_DISABLED
//
// MessageText:
//
// Exchange writer has been disabled, Whole Exchange server will not be backed up.%0
//
#define AFRES_AFVSWP_EXCHANGE_WRITER_IS_DISABLED ((DWORD)0x80190058L)

//
// MessageId: AFRES_AFVSWP_EXCH_INFOSTORE_WRITER_NOT_RUNNING
//
// MessageText:
//
// Exchange information store service is not running. Active databases will not be backed up.%0
//
#define AFRES_AFVSWP_EXCH_INFOSTORE_WRITER_NOT_RUNNING ((DWORD)0x80190059L)

//
// MessageId: AFRES_AFVSWP_EXCH_REPLICA_WRITER_NOT_RUNNING
//
// MessageText:
//
// Exchange replication service is not running. Replicated databases will not be backed up.%0
//
#define AFRES_AFVSWP_EXCH_REPLICA_WRITER_NOT_RUNNING ((DWORD)0x8019005AL)

//
// MessageId: AFRES_AFVSWP_WRITER_ERROR_RETRYABLE
//
// MessageText:
//
// The writer reports a retryable problem. Retry after %1!u! seconds. If the writer continues to return the error after several retries, try restarting the service that hosts the writer.%0
//
#define AFRES_AFVSWP_WRITER_ERROR_RETRYABLE ((DWORD)0x8019005BL)

//
// MessageId: AFRES_AFVSWP_SKIP_WRITER_IN_ERROR_STATUS
//
// MessageText:
//
// The writer "%1!s!" has been skipped during the backup process because it is in an error status.%0
//
#define AFRES_AFVSWP_SKIP_WRITER_IN_ERROR_STATUS ((DWORD)0x8019005CL)

//
// MessageId: AFRES_AFVSWP_SKIP_SQLWRITER_ON_WINDOWS_XP
//
// MessageText:
//
// SQL server is skipped because it is not supported on Windows XP.%0
//
#define AFRES_AFVSWP_SKIP_SQLWRITER_ON_WINDOWS_XP ((DWORD)0x8019005DL)

//
// MessageId: AFRES_AFVSWP_COULD_NOT_RESTORE_APPLICATION_ON_WINDOWS_XP
//
// MessageText:
//
// Application restore is not supported on Windows XP.%0
//
#define AFRES_AFVSWP_COULD_NOT_RESTORE_APPLICATION_ON_WINDOWS_XP ((DWORD)0xC019005EL)

//
// MessageId: AFRES_AFVSWP_ADD_VOLUME_TO_SNAPSHOT_FAILED_BECAUSE_NOT_EXIST
//
// MessageText:
//
// Volume cannot be added into snapshot set because it does not exist.%0
//
#define AFRES_AFVSWP_ADD_VOLUME_TO_SNAPSHOT_FAILED_BECAUSE_NOT_EXIST ((DWORD)0xC019005FL)

//
// MessageId: AFRES_AFVSWP_ADD_VOLUME_TO_SNAPSHOT_FAILED
//
// MessageText:
//
// Volume %1!s! cannot be added into snapshot set.%0
//
#define AFRES_AFVSWP_ADD_VOLUME_TO_SNAPSHOT_FAILED ((DWORD)0xC0190060L)

//
// MessageId: AFRES_AFVSWP_APPLICATION_UNABLE_LOAD_VSS_METADATA
//
// MessageText:
//
// Unable to load VSS metadata because the Microsoft VSS on the current system is not compatible with the one on the original backed up system.%0
//
#define AFRES_AFVSWP_APPLICATION_UNABLE_LOAD_VSS_METADATA ((DWORD)0xC0190061L)

//
// MessageId: AFRES_AFVSWP_SNAP_ERR_PROVIDER_NOT_REGISTER
//
// MessageText:
//
// Volume shadow copy provider specified is not registered. Please ensure all VSS relevant components are registered.%0
//
#define AFRES_AFVSWP_SNAP_ERR_PROVIDER_NOT_REGISTER ((DWORD)0xC0190062L)

//
// MessageId: AFRES_AFVSWP_BACKUP_EXCHANGE_DATABASE
//
// MessageText:
//
// Microsoft Exchange Server Mailbox Database: [%1!s!] will be backed up.%0
//
#define AFRES_AFVSWP_BACKUP_EXCHANGE_DATABASE ((DWORD)0x40190063L)

//
// MessageId: AFRES_AFVSWP_RESTORE_DELETED_EXCHANGE_DATABASE
//
// MessageText:
//
// Restore Exchange storage group/database [%1!s!] to its original location is skipped because it does not exist in the Active Directory.%0
//
#define AFRES_AFVSWP_RESTORE_DELETED_EXCHANGE_DATABASE ((DWORD)0x80190064L)

//
// MessageId: AFRES_AFVSWP_RESTORE_EXCHANGE_DATABASE_MOUNT_FAILED
//
// MessageText:
//
// Exchange storage group/database [%1!s!] has been restored to its original location, but failed to mount it.%0
//
#define AFRES_AFVSWP_RESTORE_EXCHANGE_DATABASE_MOUNT_FAILED ((DWORD)0xC0190065L)

//
// MessageId: AFRES_AFVSWP_BACKUP_EXCHANGE_PUBLIC_DATABASE
//
// MessageText:
//
// Microsoft Exchange Server Public Folder Database: [%1!s!] will be backed up.%0
//
#define AFRES_AFVSWP_BACKUP_EXCHANGE_PUBLIC_DATABASE ((DWORD)0x40190066L)

//
// MessageId: AFRES_AFVSWP_BACKUP_EXCHANGE_SKIP_DATABASE
//
// MessageText:
//
// Microsoft Exchange Server Mailbox Database: [%1!s!] is not included in backup.%0
//
#define AFRES_AFVSWP_BACKUP_EXCHANGE_SKIP_DATABASE ((DWORD)0x80190067L)

//
// MessageId: AFRES_AFVSWP_BACKUP_EXCHANGE_SKIP_PPUBLIC_DATABASE
//
// MessageText:
//
// Microsoft Exchange Server Public Folder Database: [%1!s!] is not included in backup.%0
//
#define AFRES_AFVSWP_BACKUP_EXCHANGE_SKIP_PPUBLIC_DATABASE ((DWORD)0x80190068L)

//
// MessageId: AFRES_AFVSWP_RESTORE_SKIP_MOUNTED_EXCHANGE_DATABASE
//
// MessageText:
//
// Restore of the Microsoft Exchange storage group/database [%1!s!] to its original location is skipped. It is in a Mounted status and the "Dismount the database before restore and mount the database after restore" option is unchecked.%0
//
#define AFRES_AFVSWP_RESTORE_SKIP_MOUNTED_EXCHANGE_DATABASE ((DWORD)0x80190069L)

//
// MessageId: AFRES_AFVSWP_CHECK_EXCHANGE_ACCOUNT_FAILED
//
// MessageText:
//
// The check for proper account privileges failed. Cannot access Microsoft Exchange data.%0
//
#define AFRES_AFVSWP_CHECK_EXCHANGE_ACCOUNT_FAILED ((DWORD)0xC019006AL)

//
// MessageId: AFRES_AFVSWP_EXCHANGE_ACCOUNT_NO_ENOUGH_PREVILEGE
//
// MessageText:
//
// The current account :%1!s! does not have enough privilege to access the Exchange data.  Please grant proper privilege for this account or switch to another one.%0
//
#define AFRES_AFVSWP_EXCHANGE_ACCOUNT_NO_ENOUGH_PREVILEGE ((DWORD)0xC019006BL)

//
// MessageId: AFRES_AFVSWP_EXCHANGE_RESTORE_SG_NON_EXIST
//
// MessageText:
//
// The Storage Group:[%1!s!] being restored is renamed or removed after the backup, either restore from a latest recovery point or restore to the disk%0
//
#define AFRES_AFVSWP_EXCHANGE_RESTORE_SG_NON_EXIST ((DWORD)0x8019006CL)

//
// MessageId: AFRES_AFVSWP_EXCHANGE_RESTORE_DATABASE_NON_EXIST
//
// MessageText:
//
// The Database:[%1!s!] being restored is renamed or removed after the backup, either restore from a latest recovery point or restore to the disk%0
//
#define AFRES_AFVSWP_EXCHANGE_RESTORE_DATABASE_NON_EXIST ((DWORD)0x8019006DL)

//
// MessageId: AFRES_AFVSWP_APPLICATION_RESTORE_INCOMPLETE
//
// MessageText:
//
// Application restore is incomplete%0
//
#define AFRES_AFVSWP_APPLICATION_RESTORE_INCOMPLETE ((DWORD)0x8019006EL)

//
// MessageId: AFRES_AFVSWP_EXCHANGE_DATABASE_MOUNT_FAILED
//
// MessageText:
//
// Failed to mount Microsoft Exchange database: [%1!s!].%0
//
#define AFRES_AFVSWP_EXCHANGE_DATABASE_MOUNT_FAILED ((DWORD)0xC019006FL)

//
// MessageId: AFRES_AFVSWP_SNAPSHOT_NOT_SUPPORT_ON_VOLUME
//
// MessageText:
//
// Snapshot storage area for volume is not on NTFS volume. The snapshot storage area will be switched to another volume to continue the backup. (Storage Area=[%1!s!], Volume Name=[%2!s!])%0
//
#define AFRES_AFVSWP_SNAPSHOT_NOT_SUPPORT_ON_VOLUME ((DWORD)0x80190070L)

//
// MessageId: AFRES_AFVSWP_SNAPSHOT_AREA_INFORMATION
//
// MessageText:
//
// Snapshot storage Area for volume [%1!s!] is on volume [%2!s!].%0
//
#define AFRES_AFVSWP_SNAPSHOT_AREA_INFORMATION ((DWORD)0x40190071L)

//
// MessageId: AFRES_AFVSWP_FAILED_TO_INIT_VSS_BACKUP_MANAGER
//
// MessageText:
//
// Failed to initialize backup manager, please refer to Windows Event log for more details. (EC=[0x%1!08x!])%0
//
#define AFRES_AFVSWP_FAILED_TO_INIT_VSS_BACKUP_MANAGER ((DWORD)0xC0190072L)

//
// MessageId: AFRES_AFVSWP_ORACLE_WRITER_IS_NOT_INSTALLED
//
// MessageText:
//
// The Oracle VSS Writer service "%1!s!" is not installed, the Oracle database "%2!s!" will be skipped to back up. If you want to back it up, please install the writer and make sure it is running.%0
//
#define AFRES_AFVSWP_ORACLE_WRITER_IS_NOT_INSTALLED ((DWORD)0x80190073L)

//
// MessageId: AFRES_AFVSWP_ORACLE_WRITER_IS_NOT_RUNNING
//
// MessageText:
//
// The Oracle VSS Writer service "%1!s!" will be started automatically to back up the Oracle database "%2!s!".%0
//
#define AFRES_AFVSWP_ORACLE_WRITER_IS_NOT_RUNNING ((DWORD)0x80190074L)

//
// MessageId: AFRES_AFVSWP_ORACLE_WRITER_IS_REQUIRED_FOR_BACKUP
//
// MessageText:
//
// The Oracle VSS Writer service "%1!s!" must be started in order to complete the Oracle database "%2!s!" backup.%0
//
#define AFRES_AFVSWP_ORACLE_WRITER_IS_REQUIRED_FOR_BACKUP ((DWORD)0x80190075L)

//
// MessageId: AFRES_AFVSWP_TAKE_SNAPSHOT_TIMEOUT
//
// MessageText:
//
// Volume Shadow Copy service take snapshot timeout, [%1!u!] ms.%0
//
#define AFRES_AFVSWP_TAKE_SNAPSHOT_TIMEOUT ((DWORD)0xC0190076L)

//
// MessageId: AFRES_AFVSWP_NO_ENOUGH_FREE_SPACE_FOR_SNAP_AREA
//
// MessageText:
//
// Unable to find available volume with enough free space to hold storage area for volume [%1!s!].%0
//
#define AFRES_AFVSWP_NO_ENOUGH_FREE_SPACE_FOR_SNAP_AREA ((DWORD)0xC0190077L)

//
// MessageId: AFRES_AFVSWP_UNABLE_QUERY_DB_BECAUSE_NO_DC_OR_ACCOUNT_NO_RIGHT
//
// MessageText:
//
// Unable to query the database, please check if the Domain Controller (DC) is accessible and the current account "%1!s!" has privileges to query the Active Directory (AD). (EC=[0x%2!08x!]).%0
//
#define AFRES_AFVSWP_UNABLE_QUERY_DB_BECAUSE_NO_DC_OR_ACCOUNT_NO_RIGHT ((DWORD)0xC0190078L)

//
// MessageId: AFRES_AFVSWP_UNABLE_QUERY_DB_BECAUSE_NO_DC_OR_NO_RIGHT
//
// MessageText:
//
// Unable to query the database, please check if the Domain Controller (DC) is accessible and the current account has privileges to query the Active Directory (AD). (EC=[0x%1!08x!]).%0
//
#define AFRES_AFVSWP_UNABLE_QUERY_DB_BECAUSE_NO_DC_OR_NO_RIGHT ((DWORD)0xC0190079L)

 // The following are the message definitions for WebService
//
// MessageId: AFRES_AFJWBS_BKCFG_CHAGNED
//
// MessageText:
//
// Backup configuration modified.%0
//
#define AFRES_AFJWBS_BKCFG_CHAGNED       ((DWORD)0x801A0001L)

//
// MessageId: AFRES_AFJWBS_EMAIL_SEND
//
// MessageText:
//
// Email has been successfully sent.%0
//
#define AFRES_AFJWBS_EMAIL_SEND          ((DWORD)0x401A0002L)

//
// MessageId: AFRES_AFJWBS_JOB_SKIPPED
//
// MessageText:
//
// A job is currently running. A new job named "%1!s!" at %2!s! cannot be performed.%0
//
#define AFRES_AFJWBS_JOB_SKIPPED         ((DWORD)0xC01A0003L)

//
// MessageId: AFRES_AFJWBS_JOB_RETRY
//
// MessageText:
//
// %1!s!%0
//
#define AFRES_AFJWBS_JOB_RETRY           ((DWORD)0x401A0004L)

//
// MessageId: AFRES_AFJWBS_GENERAL
//
// MessageText:
//
// %1!s!%0
//
#define AFRES_AFJWBS_GENERAL             ((DWORD)0x401A0005L)

//
// MessageId: AFRES_AFJWBS_JOB_FULL_ADAYS
//
// MessageText:
//
// License failure. The schedule intervals of full backup job has been adjusted to %1!s! days.%0
//
#define AFRES_AFJWBS_JOB_FULL_ADAYS      ((DWORD)0xC01A0006L)

//
// MessageId: AFRES_AFJWBS_JOB_FULL_AHOURS
//
// MessageText:
//
// License failure. The schedule intervals of full backup job has been adjusted to %1!s! hours.%0
//
#define AFRES_AFJWBS_JOB_FULL_AHOURS     ((DWORD)0xC01A0007L)

//
// MessageId: AFRES_AFJWBS_JOB_FULL_AMINS
//
// MessageText:
//
// License failure. The schedule intervals of full backup job has been adjusted to %1!s! minutes.%0
//
#define AFRES_AFJWBS_JOB_FULL_AMINS      ((DWORD)0xC01A0008L)

//
// MessageId: AFRES_AFJWBS_JOB_INC_AHOURS
//
// MessageText:
//
// License failure. The schedule intervals of incremental backup job has been adjusted to %1!s! hours.%0
//
#define AFRES_AFJWBS_JOB_INC_AHOURS      ((DWORD)0xC01A0009L)

//
// MessageId: AFRES_AFJWBS_JOB_INC_AMINS
//
// MessageText:
//
// License failure. The schedule intervals of incremental backup job has been adjusted to %1!s! minutes.%0
//
#define AFRES_AFJWBS_JOB_INC_AMINS       ((DWORD)0xC01A000AL)

//
// MessageId: AFRES_AFJWBS_JOB_VSPHERE_LICENSE_FAILED
//
// MessageText:
//
// Host-Based VM backup job failed because of a license failure. Contact your account representative to obtain a new license.%0
//
#define AFRES_AFJWBS_JOB_VSPHERE_LICENSE_FAILED ((DWORD)0xC01A000BL)

//
// MessageId: AFRES_AFJWBS_VSPHERE_VMTOOL_ERROR
//
// MessageText:
//
// Could not check VMware Tools state.%0
//
#define AFRES_AFJWBS_VSPHERE_VMTOOL_ERROR ((DWORD)0xC01A000CL)

//
// MessageId: AFRES_AFJWBS_VSPHERE_VMTOOL_NOT_INSTALL
//
// MessageText:
//
// VMware Tools is not installed.%0
//
#define AFRES_AFJWBS_VSPHERE_VMTOOL_NOT_INSTALL ((DWORD)0xC01A000DL)

//
// MessageId: AFRES_AFJWBS_VSPHERE_VMTOOL_OUT_OF_DATE
//
// MessageText:
//
// VMware Tools is out of date.%0
//
#define AFRES_AFJWBS_VSPHERE_VMTOOL_OUT_OF_DATE ((DWORD)0xC01A000EL)

//
// MessageId: AFRES_AFJWBS_JOB_VSPHERE_HOST_NOT_FOUND
//
// MessageText:
//
// Unable to communicate with server  %3!s!.%0
//
#define AFRES_AFJWBS_JOB_VSPHERE_HOST_NOT_FOUND ((DWORD)0xC01A000FL)

//
// MessageId: AFRES_AFJWBS_VSPHERE_VIX_NOT_INSTALL
//
// MessageText:
//
// VMware VIX is not installed. The application cannot perform application log truncation and Pre/Post commands without VMware VIX.%0
//
#define AFRES_AFJWBS_VSPHERE_VIX_NOT_INSTALL ((DWORD)0xC01A0010L)

//
// MessageId: AFRES_AFJWBS_VSPHERE_VIX_OUT_OF_DATE
//
// MessageText:
//
// VMware VIX version is lower than 1.1.0. The application cannot perform application log truncation and Pre/Post commands.%0
//
#define AFRES_AFJWBS_VSPHERE_VIX_OUT_OF_DATE ((DWORD)0xC01A0011L)

//
// MessageId: AFRES_AFJWBS_D2D_VERSION_INFO
//
// MessageText:
//
// Current %1!s! version: %2!s! (Build %3!s!.%4!s!.%5!s!)%0
//
#define AFRES_AFJWBS_D2D_VERSION_INFO    ((DWORD)0x401A0012L)

//
// MessageId: AFRES_AFJWBS_VSPHERE_LICENSE_FAILED_CANNOT_CONNECT
//
// MessageText:
//
// Failed to connect to %1!s! to check license,please make sure %2!s! can be connected.%0
//
#define AFRES_AFJWBS_VSPHERE_LICENSE_FAILED_CANNOT_CONNECT ((DWORD)0xC01A0013L)

//
// MessageId: AFRES_AFJWBS_D2D_VERSION_INFO_WITH_UPDATE
//
// MessageText:
//
// Current %1!s! version: %2!s! (Build %3!s!.%4!s!.%5!s!) Update %6!s!%0
//
#define AFRES_AFJWBS_D2D_VERSION_INFO_WITH_UPDATE ((DWORD)0x401A0014L)

//
// MessageId: AFRES_AFJWBS_VSPHERE_LICENSE_FAILED_COPY_JOB
//
// MessageText:
//
// Host-Based VM copy job failed because of a license failure. Contact your account representative to obtain a new license.%0
//
#define AFRES_AFJWBS_VSPHERE_LICENSE_FAILED_COPY_JOB ((DWORD)0xC01A0015L)

//
// MessageId: AFRES_DATA_SYNC_GET_BK_DATA_FAILURE
//
// MessageText:
//
// The synchronization process was unable to retrieve the data for the backup job.%0
//
#define AFRES_DATA_SYNC_GET_BK_DATA_FAILURE ((DWORD)0xC01A0016L)

//
// MessageId: AFRES_DATA_SYNC_GET_VM_DATA_FAILURE
//
// MessageText:
//
// The synchronization process was unable to retrieve the data for the ^AU_ProductName_HBVB_SHORT^ job.%0
//
#define AFRES_DATA_SYNC_GET_VM_DATA_FAILURE ((DWORD)0xC01A0017L)

//
// MessageId: AFRES_DATA_SYNC_GET_VCM_DATA_FAILURE
//
// MessageText:
//
// The synchronization process was unable to retrieve the ^AU_ProductName_VS_SHORT^ data.%0
//
#define AFRES_DATA_SYNC_GET_VCM_DATA_FAILURE ((DWORD)0xC01A0018L)

//
// MessageId: AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE
//
// MessageText:
//
// The synchronization process was unable to retrieve the Activity Log data.%0
//
#define AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE ((DWORD)0xC01A0019L)

//
// MessageId: AFRES_DATA_SYNC_GET_FILECOPY_DATA_FAILURE
//
// MessageText:
//
// The synchronization process was unable to retrieve the data for the file copy job.%0
//
#define AFRES_DATA_SYNC_GET_FILECOPY_DATA_FAILURE ((DWORD)0xC01A001AL)

//
// MessageId: AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE
//
// MessageText:
//
// Cannot log in to the ^AU_ProductName_CONSOLE_SHORT^ service on node %1!s!. The problem may occur when ^AU_ProductName_CONSOLE_SHORT^ is reinstalled. To correct this problem, open ^AU_ProductName_CONSOLE_SHORT^, delete the ^AU_ProductName_AGENT^ node, and then add the ^AU_ProductName_AGENT^ node.%0
//
#define AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE ((DWORD)0xC01A001BL)

//
// MessageId: AFRES_DATA_SYNC_CONNECT_TO_EDGE_FAILURE
//
// MessageText:
//
// The ^AU_ProductName_AGENT^ service cannot communicate with the ^AU_ProductName_CONSOLE_SHORT^ service running on node %1!s!. This problem can occur when the ^AU_ProductName_CONSOLE_SHORT^ service is not running, or, the ^AU_ProductName_AGENT^ node cannot communicate with the ^AU_ProductName_CONSOLE_SHORT^ node using the host name of the ^AU_ProductName_CONSOLE_SHORT^ node.%0
//
#define AFRES_DATA_SYNC_CONNECT_TO_EDGE_FAILURE ((DWORD)0xC01A001CL)

//
// MessageId: AFRES_DATA_SYNC_EDGE_NOT_MATCH
//
// MessageText:
//
// The ^AU_ProductName_AGENT^ service cannot communicate with the ^AU_ProductName_CONSOLE_SHORT^ service running on node %1!s!. The version of ^AU_ProductName_AGENT^ is not compatible with the version of ^AU_ProductName_CONSOLE_SHORT^.%0
//
#define AFRES_DATA_SYNC_EDGE_NOT_MATCH   ((DWORD)0xC01A001DL)

//
// MessageId: AFRES_DATA_SYNC_BK_TO_EDGE_FAILURE
//
// MessageText:
//
// The synchronization process was unable to synchronize the Backup job data on ^AU_ProductName_CONSOLE_SHORT^ node %1!s!.%0
//
#define AFRES_DATA_SYNC_BK_TO_EDGE_FAILURE ((DWORD)0xC01A001EL)

//
// MessageId: AFRES_DATA_SYNC_VM_TO_EDGE_FAILURE
//
// MessageText:
//
// The synchronization process was unable to synchronize the ^AU_ProductName_HBVB_SHORT^ job data on ^AU_ProductName_CONSOLE_SHORT^ node %1!s!.%0
//
#define AFRES_DATA_SYNC_VM_TO_EDGE_FAILURE ((DWORD)0xC01A001FL)

//
// MessageId: AFRES_DATA_SYNC_VCM_TO_EDGE_FAILURE
//
// MessageText:
//
// The synchronization process was unable to synchronize the ^AU_ProductName_VS_SHORT^ data on ^AU_ProductName_CONSOLE_SHORT^ node %1!s!.%0
//
#define AFRES_DATA_SYNC_VCM_TO_EDGE_FAILURE ((DWORD)0xC01A0020L)

//
// MessageId: AFRES_DATA_SYNC_LOG_TO_EDGE_FAILURE
//
// MessageText:
//
// The synchronization process was unable to synchronize the Activity Log data on ^AU_ProductName_CONSOLE_SHORT^ node %1!s!.%0
//
#define AFRES_DATA_SYNC_LOG_TO_EDGE_FAILURE ((DWORD)0xC01A0021L)

//
// MessageId: AFRES_DATA_SYNC_FILECOPY_TO_EDGE_FAILURE
//
// MessageText:
//
// The synchronization process was unable to synchronize the File Copy job data on ^AU_ProductName_CONSOLE_SHORT^ node %1!s!.%0
//
#define AFRES_DATA_SYNC_FILECOPY_TO_EDGE_FAILURE ((DWORD)0xC01A0022L)

//
// MessageId: AFRES_DATA_SYNC_NOT_MANAGED
//
// MessageText:
//
// The synchronization process cannot synchronize data with the ^AU_ProductName_CONSOLE_SHORT^. The node is not managed by a ^AU_ProductName_CONSOLE_SHORT^ product.%0
//
#define AFRES_DATA_SYNC_NOT_MANAGED      ((DWORD)0xC01A0023L)

//
// MessageId: AFRES_DATA_SYNC_UNKNOWN_ERROR
//
// MessageText:
//
// The synchronization process was unable to synchronize data with ^AU_ProductName_CONSOLE_SHORT^ on node %1!s!.%0
//
#define AFRES_DATA_SYNC_UNKNOWN_ERROR    ((DWORD)0xC01A0024L)

//
// MessageId: AFRES_AFJWBS_VSPHERE_FAILED_COPY_JOB_NO_VOLUMN
//
// MessageText:
//
// Failed to copy the recovery point for virtual machine %1!s!. The recovery point of session number %2!s! contains no volume information.%0
//
#define AFRES_AFJWBS_VSPHERE_FAILED_COPY_JOB_NO_VOLUMN ((DWORD)0x801A0025L)

//
// MessageId: AFRES_AFJWBS_VSPHERE_HBBU_PROXY_INSTALL_VOLUME_FREE_SPACE_ALERT
//
// MessageText:
//
// In the backup proxy server %1!s!, the volume %2!s! (where the ^AU_ProductName_AGENT^ is installed) has reached the free space threshold %3!s! MB. Release some space otherwise backup may stop working.%0
//
#define AFRES_AFJWBS_VSPHERE_HBBU_PROXY_INSTALL_VOLUME_FREE_SPACE_ALERT ((DWORD)0x801A0026L)

 //