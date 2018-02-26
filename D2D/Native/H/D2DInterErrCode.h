#pragma once

#include "D2DErrorCode.h"

////////////////////////////////////////////////////////////////////
//ZZ: Some error codes in interface.
#define D2DINTER_ERROR_CODE_BASE                      D2DERRCODEBASE(D2D_FACILITY_INTERFACE, 0)

//
// MessageId: D2DINTER_E_UNABLE_TO_CREATE_KEYMGMT_INSTACE
//
// MessageText:
//
//  Failed to create key management instance.
//
#define D2DINTER_E_UNABLE_TO_CREATE_KEYMGMT_INSTACE   D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 1)  

//
// MessageId: D2DINTER_E_UNABLE_TO_GET_ADMIN_INFO
//
// MessageText:
//
//  Failed to get D2D administration acccount from registry table.
//
#define D2DINTER_E_UNABLE_TO_GET_ADMIN_INFO           D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 2)  

//
// MessageId: D2DINTER_E_UNABLE_TO_GET_KEYMGMT_PATH
//
// MessageText:
//
//  Faile to get key management file path.
//
#define D2DINTER_E_UNABLE_TO_GET_KEYMGMT_PATH         D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 3)  

//
// MessageId: D2DINTER_E_FAILED_TO_VALIDATE_MASTER_KEY
//
// MessageText:
//
//  Failed to validate master key with the one in key management file.
//
#define D2DINTER_E_FAILED_TO_VALIDATE_MASTER_KEY      D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 4)  

//
// MessageId: D2DINTER_E_MASTER_KEY_MISMATCHED
//
// MessageText:
//
//  The master key of current machine is not matched with the one in key management file.
//
#define D2DINTER_E_MASTER_KEY_MISMATCHED              D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 5)  

//
// MessageId: D2DINTER_E_UNABLE_TO_RENAME_KYYMGMT
//
// MessageText:
//
//  Failed to remove specified session password from key management file.
//
#define D2DINTER_E_UNABLE_TO_RENAME_KEYMGMT           D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 6)  

//
// MessageId: D2DINTER_E_UNABLE_TO_ADD_SESSPWD
//
// MessageText:
//
//  Failed to add session password to key management file.
//
#define D2DINTER_E_UNABLE_TO_ADD_SESSPWD              D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 7)  

//
// MessageId: D2DINTER_E_UNABLE_TO_RESTRIVE_SESSPWD
//
// MessageText:
//
//  Uable to find session password in key management file.
//
#define D2DINTER_E_UNABLE_TO_RESTRIVE_SESSPWD         D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 8)  

//
// MessageId: D2DINTER_E_UNABLE_TO_REMOVE_SESSPWD
//
// MessageText:
//
//  Failed to remove session password from key management.
//
#define D2DINTER_E_UNABLE_TO_REMOVE_SESSPWD           D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 9)  

//
// MessageId: D2DINTER_E_UNABLE_TO_SHRINK_KEYMGMT_FILE
//
// MessageText:
//
//  Failed to shrink key management file.
//
#define D2DINTER_E_UNABLE_TO_SHRINK_KEYMGMT_FILE      D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 10)  

//
// MessageId: D2DINTER_E_UNABLE_TO_UPDATE_MASTER_KEY
//
// MessageText:
//
//  Unable to update master key using current administration account.
//
#define D2DINTER_E_UNABLE_TO_UPDATE_MASTER_KEY        D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 11)  

//
// MessageId: D2DINTER_E_AFSTOR_UNABLE_TO_DELETE_SESSION
//
// MessageText:
//
//  Unable to delete session using AFStor.
//
#define D2DINTER_E_AFSTOR_UNABLE_TO_DELETE_SESSION    D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 12)  

//
// MessageId: D2DINTER_E_KEYMGMT_IS_DISABLED
//
// MessageText:
//
//  Key management is disabled, We will neither saving nor retrieving password.
//
#define D2DINTER_E_KEYMGMT_IS_DISABLED   D2DERRCODEGENBYIDX(D2DINTER_ERROR_CODE_BASE, 13)  
