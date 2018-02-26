#pragma once

#include "D2DErrorCode.h"

#define MERGE_ERR2D2D(hr)                  ERROR2D2D(D2D_FACILITY_MERGE, (hr))
#define MERGE_HR2D2D(hr)                   HRESULT2D2D(D2D_FACILITY_MERGE, (hr))

#define D2DMERGE_SYSTEM_WRAP_ERRCODE_BASE  MERGE_ERR2D2D(0)
///ZZ: Reserve 20000 error code for system problem. internal error starts from 20001.
#define D2DMERGE_INTERNALE_ERRCODE_BASE    MERGE_ERR2D2D(0x4E20)


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//
// MessageId: D2DMERGE_S_OK
//
// MessageText:
//
//  Everything is fine.
//
#define D2DMERGE_S_OK                                     0x0

//
// MessageId: D2DMERGE_E_MORE_DATA
//
// MessageText:
//
//  The input buffer is insufficient to store all data.
//
#define D2DMERGE_E_MORE_DATA                              D2DERRCODEGENBYIDX(D2DMERGE_SYSTEM_WRAP_ERRCODE_BASE, ERROR_MORE_DATA)

//
// MessageId: D2DMERGE_E_INVALID_PARAM
//
// MessageText:
//
//  The input parameter for function is invalid.
//
#define D2DMERGE_E_INVALID_PARAM                          D2DERRCODEGENBYIDX(D2DMERGE_SYSTEM_WRAP_ERRCODE_BASE, ERROR_INVALID_PARAMETER)


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//
// MessageId: D2DMERGE_E_JOB_IS_STOPPED_OR_CACELED
//
// MessageText:
//
//  Job is stopped or canceled.
//
#define D2DMERGE_E_JOB_IS_STOPPED_OR_CACELED              D2DERRCODEGENBYIDX(D2DMERGE_INTERNALE_ERRCODE_BASE, 1)

//
// MessageId: D2DMERGE_E_JOB_STOPPED_OR_CACELLED
//
// MessageText:
//
//  Job is stopped or canceled.
//
#define D2DMERGE_E_JOB_IS_SKIPPED                         D2DERRCODEGENBYIDX(D2DMERGE_INTERNALE_ERRCODE_BASE, 2)

//
// MessageId: D2DMERGE_E_JOB_FAILED_TO_LOCK_SESS
//
// MessageText:
//
//  Job failed because unable to lock session.
//
#define D2DMERGE_E_JOB_FAILED_TO_LOCK_SESS                D2DERRCODEGENBYIDX(D2DMERGE_INTERNALE_ERRCODE_BASE, 3)

//
// MessageId: D2DMERGE_E_NO_ENOUGH_FREE_SPACE_FOR_OPERATION
//
// MessageText:
//
//  There is no engouh free space for merge or other operation
//
#define D2DMERGE_E_NO_ENOUGH_FREE_SPACE_FOR_OPERATION     D2DERRCODEGENBYIDX(D2DMERGE_INTERNALE_ERRCODE_BASE, 4)

//
// MessageId: D2DMERGE_E_JOB_SESS_LOCK_BY_FILECOPY
//
// MessageText:
//
//  Job failed because session is locked by filecopy.
//
#define D2DMERGE_E_JOB_SESS_LOCK_BY_FILECOPY			  D2DERRCODEGENBYIDX(D2DMERGE_INTERNALE_ERRCODE_BASE, 5)

//
// MessageId: D2DMERGE_E_NO_ENOUGH_SESS_2_MERGE
//
// MessageText:
//
//  No enough session to merge.
//
#define D2DMERGE_E_NO_ENOUGH_SESS_2_MERGE  			      D2DERRCODEGENBYIDX(D2DMERGE_INTERNALE_ERRCODE_BASE, 6)