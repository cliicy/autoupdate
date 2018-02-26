#ifndef _UNIV_DBDEFS_H_
#define	_UNIV_DBDEFS_H_

#ifdef __cplusplus
extern "C" {
#endif

#include "univdefs.h"
#include <asdefs.h>



/* Definitions for Queue RPC  procedures */

#define  ASUNIRPC_PROC_UNIOpenDB                   1
#define  ASUNIRPC_PROC_UNICloseDB                  2
#define  ASUNIRPC_PROC_UNIGetNumberOfRecordsInDB   3
#define  ASUNIRPC_PROC_UNIGetTapeList              4
#define  ASUNIRPC_PROC_UNIGetSessionList           5
#define  ASUNIRPC_PROC_UNIGetDetailList            6
#define  ASUNIRPC_PROC_UNIGetServerList            7
#define  ASUNIRPC_PROC_UNIGetVolumeList            8
#define  ASUNIRPC_PROC_UNIGetFileList              9
#define  ASUNIRPC_PROC_UNIGetVersionHistory        10
#define  ASUNIRPC_PROC_UNIGetSessionListByID       11
#define  ASUNIRPC_PROC_UNIGetDetailListByID        12
#define  ASUNIRPC_PROC_UNIGetObjectItem            13
#define  ASUNIRPC_PROC_UNIGetObjectID              14
#define  ASUNIRPC_PROC_UNIGetRecordList            15
#define  ASUNIRPC_PROC_UNIGetDiskSpaceInfo         16
#define  ASUNIRPC_PROC_UNIGetDBConfiguration       17
#define  ASUNIRPC_PROC_UNIUpdateDBConfiguration    18
#define  ASUNIRPC_PROC_UNIUpdateRecord             19
#define  ASUNIRPC_PROC_UNIDeleteRecord             20
#define  ASUNIRPC_PROC_UNIInsertRecord             21
#define  ASUNIRPC_PROC_UNIGetQueryFileList         22
#define  ASUNIRPC_PROC_UNIGetQueryDataList         23
#define  ASUNIRPC_PROC_UNIGetWSCountList           24
#define  ASUNIRPC_PROC_UNIDoesNodeExist            25
#define  ASUNIRPC_PROC_UNIGetASConfig              26
#define  ASUNIRPC_PROC_UNISetASConfig              27
#define	ASUNIRPC_PROC_UNIGetSessAndTapeInfoByID   28


#ifdef __cplusplus
}
#endif

#endif /* !_UNIV_DBDEFS_H_ */


