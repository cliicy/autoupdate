#pragma once

//////////////////////////////////////////////////////////////////////////
//Server status
//Native status from GDD server
const int GDD_SERVER_STOPPED = 1;
const int GDD_SERVER_INITIALIZING = 2;
const int GDD_SERVER_RUNNING = 3;
const int GDD_SERVER_STOPPING = 4;
const int GDD_SERVER_ABNORMAL_BLOCK_ANY = 5;
const int GDD_SERVER_ABNORMAL_RESTORE_ONLY = 6;
//Extend status for data store control the GDD server
const int GDD_SERVER_UNKNOWN = 7;	
const int GDD_SERVER_MODIFYING = 8;
const int GDD_SERVER_DELETING = 9;
const int GDD_SERVER_DELETED = 10;
const int GDD_SERVER_MODIFYCOPYING = 11;
//Server status end
//////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////
//GDD server error code for status change.
//Native error code from GDD server side.
const int ERR_OK = 0;
const int ERR_DATA_DISK_FULL = 1;
const int ERR_INDEX_DISK_FULL = 2;
const int ERR_DATA_DISK_INACCESS = 3;
const int ERR_INDEX_DISK_INACCESS = 4;
const int ERR_NO_MEMORY = 5;

const int ERR_HASH_DATA_FULL = 7;		//the total hash entry size reaches the assigned value, please assign more memory or disk. The server can do restore job only.
const int ERR_HASH_DISK_FULL = 8;		//the SSD/Disk of hash role is full
const int ERR_HASH_DISK_INACCESS = 9;
const int ERR_HASH_FILE_ERROR = 10;
const int ERR_HASH_OTHER_ERROR = 11;

const int ERR_RECLAIM_ERROR = 16;

//
//Extend error code for data store 
const int ERR_GDD_STATUS_ERROR_EXTEND_BASE =  1000;
const int ERR_GDD_STATUS_PRIMARY_SERVER_DOWN = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 1;
const int ERR_GDD_STATUS_PRIMARY_SERVER_FAIL_CONNECT = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 2;
const int ERR_GDD_STATUS_DATA_SERVER_DOWN = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 3;
const int ERR_GDD_STATUS_DATA_SERVER_FAIL_CONNECT = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 4;
const int ERR_GDD_STATUS_HASH_SERVER_DOWN = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 5;
const int ERR_GDD_STATUS_HASH_SERVER_FAIL_CONNECT = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 6;

const int ERR_GDD_STATUS_LOCAL_COMMPATH_INACCESSIBLE = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 7;
const int ERR_GDD_STATUS_REMOTE_COMMPATH_INACCESSIBLE = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 8;

const int ERR_GDD_STATUS_CONFIGFILE_INACCESSIBLE = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 9;
const int ERR_GDD_STATUS_VERIFYFILE_INACCESSIBLE = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 10;

const int ERR_GDD_STATUS_CONF_REG_CORRUPT = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 11;
const int ERR_GDD_STATUS_CONF_FILE_CORRUPT = ERR_GDD_STATUS_ERROR_EXTEND_BASE + 12;

//////////////////////////////////////////////////////////////////////////

//HashDB error code
#define FC_OK				 0

#define HASHDB_ERR_BASE         (0xF00F0000)

#define FC_FILE_ERROR			(HASHDB_ERR_BASE + 1)
#define FC_DISK_INACCESS		(HASHDB_ERR_BASE + 2)
#define FC_DISK_FULL			(HASHDB_ERR_BASE + 3)

#define FC_FILE_NOT_FOUND		(HASHDB_ERR_BASE + 4)

#define FC_ALLOC_MEM_ERROR		(HASHDB_ERR_BASE + 5)

#define FC_MEMORY_TOO_SMALL		(HASHDB_ERR_BASE + 6)
#define FC_MEM_OVER_PHYSICAL	(HASHDB_ERR_BASE + 7)

#define FC_INTERNAL_ERROR		(HASHDB_ERR_BASE + 8)
#define FC_INVALID_PARAM		(HASHDB_ERR_BASE + 9)
#define FC_CANNT_FOUND_PAGE		(HASHDB_ERR_BASE + 10)
#define FC_THREAD_ERROR			(HASHDB_ERR_BASE + 11)
#define FC_CHECKSUM_ERROR		(HASHDB_ERR_BASE + 12)

#define FC_MERGE_ERROR			(HASHDB_ERR_BASE + 13)
#define HASH_MAP_PATH_INVAILD	(HASHDB_ERR_BASE + 14)
#define FC_HASH_DATA_FULL		(HASHDB_ERR_BASE + 15)

#define FC_HASH_INITIALIZING	(HASHDB_ERR_BASE + 20)
#define FC_HASH_INIT_FAILED		(HASHDB_ERR_BASE + 21)
#define FC_HASH_QUITTING		(HASHDB_ERR_BASE + 22)
