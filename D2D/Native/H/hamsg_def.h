#ifndef _REP_RES_DEFINE_H_
#define _REP_RES_DEFINE_H_

#define AFRES_AFREPC 0x00004000

#define AFRES_AFREPC_JOB_START              (AFRES_AFREPC + 0)
#define AFRES_AFREPC_SRC_DES                (AFRES_AFREPC + 1)
#define AFRES_AFREPC_JOB_FINISHED           (AFRES_AFREPC + 2)
#define AFRES_AFREPC_JOB_FAILED             (AFRES_AFREPC + 3)
#define AFRES_AFREPC_JOB_CANCELED           (AFRES_AFREPC + 4)
#define AFRES_AFREPC_JOB_SUMMARY            (AFRES_AFREPC + 5)
#define AFRES_AFREPC_SESS_SKIP              (AFRES_AFREPC + 6)
#define AFRES_AFREPC_SOCK_CONN_SUCC         (AFRES_AFREPC + 7)
#define AFRES_AFREPC_SHAR_CONN_SUCC         (AFRES_AFREPC + 8)
#define AFRES_AFREPC_SOCK_CONN_FAILED       (AFRES_AFREPC + 9)
#define AFRES_AFREPC_SHAR_CONN_FAILED       (AFRES_AFREPC + 0x0A)
#define AFRES_AFREPC_SOCK_FAIL_AUTH         (AFRES_AFREPC + 0x0B)
#define AFRES_AFREPC_SRC_INFO               (AFRES_AFREPC + 0x0C)
#define AFRES_AFREPC_SOCK_VHD_CMPRS_VOL     (AFRES_AFREPC + 0x0D)
#define AFRES_AFREPC_SHAR_VHD_CMPRS_VOL     (AFRES_AFREPC + 0x0E)
#define AFRES_AFREPC_SRC_LOCK_FAILED        (AFRES_AFREPC + 0x0F)
#define AFRES_AFREPC_DES_NO_ENOUGH_SPACE    (AFRES_AFREPC + 0x10)
#define AFRES_AFREPC_SRV_STOPPING           (AFRES_AFREPC + 0x11) //
#define AFRES_AFREPC_NETWORK_ERROR          (AFRES_AFREPC + 0x12)
#define AFRES_AFREPC_WINDOWS_ERROR          (AFRES_AFREPC + 0x16)


//Below the VDDK error message ID
#define AFRES_VDDK_NETWORK_ERROR      20000    //Please check Monitee and ESX/VC are connected to network
#define AFRES_VDDK_VM_ERROR           21000    //Please remove existing standby VM and trigger a new virtual standby job
#define AFRES_VDDK_DISK_ERROR         22000    //Please do full backup
#define AFRES_VDDK_CREDENTIAL_ERROR   23000    //Check username and password
#define AFRES_VDDK_INTERNAL_ERROR     25000    //Contact technical support
#define AFRES_VDDK_SPACE_ERROR        26000    //There is not enough space in data store, please free some space
#define AFRES_VDDK_VMDKFILE_ERROR     27000    //Disk file is bigger than 2TB
#define AFRES_VDDK_LICENSE_ERROR      28000    //Check license is installed.

#endif //_REP_RES_DEFINE_H_




//////////////////////////////////////////////////////////////////////////
// Description
//////////////////////////////////////////////////////////////////////////
/*
AFRES_AFREPC_JOB_START      "Offline copy job started."
AFRES_AFREPC_SRC_DES        "Source path is %s, destination root path is %s."
AFRES_AFREPC_JOB_FINISHED   "Offline copy job finished."
AFRES_AFREPC_JOB_FAILED     "Offline copy job failed."
AFRES_AFREPC_JOB_CANCELED   "Offline copy job is canceled."
AFRES_AFREPC_JOB_SUMMARY    "Total processed data size %s, elapsed time %s, average throughput %s/Min."
AFRES_AFREPC_SESS_SKIP      "This session already exists on destination, skip it."
AFRES_AFREPC_SOCK_CONN_SUCC   "Connected to remote server, server = %s"
AFRES_AFREPC_SHAR_CONN_SUCC   "Connected to share folder, folder = %s"
AFRES_AFREPC_SOCK_CONN_FAILED "Failed to connect to remote server, server = %s, port = %s."
AFRES_AFREPC_SHAR_CONN_FAILED "Failed to connect to share folder, folder = %s, user = %s."
AFRES_AFREPC_SOCK_FAIL_AUTH "Failed to authenticate user for socket, user = %s."
AFRES_AFREPC_SRC_INFO       "Source directory is %s D2D session" //compressed or plain
AFRES_AFREPC_SOCK_VHD_CMPRS_VOL "Virtual Standby does not support creating virtual disk images on compressed volumes and volumes that are encrypted by the file system. 
                                 (host = %s, directory = %s)"
AFRES_AFREPC_SHAR_VHD_CMPRS_VOL "Do not support create virtual disk image on compress volume. 
                                 (directory = %s)"

AFRES_AFREPC_SRC_LOCK_FAILED    "Cannot lock the source session root directory for read, root = %s"

AFRES_AFREPC_DES_NOT_ENOUGH_SPACE "There is no enough free disk space on monitor, monitor = %s, folder = %s."
AFRES_AFREPC_SRV_STOPPING         "The virtual standby conversion job was interrupted. The job was interrupted because the user or a Setup application stopped the CA ARCserve D2D web service on the monitor server. (Monitor = %s)"
AFRES_AFREPC_NETWORK_ERROR        "The monitor server cannot communicate with the node. Verify that monitor server and the node can communicate via the network."
*/