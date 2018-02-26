/*HEADER1.10*******************************************************************
; 
; © Copyright 2016 Arcserve, including its affiliates and subsidiaries.
;All rights reserved.  Any third party trademarks or copyrights are the
;property of their respective owners.
;
; Include file    - FCRinst.h
; Collection name - FCR
; Module name     - FCR installation
;
;******************************************************************************
;
; Revision -        Date    Author  Description
;               
;
; Description - 
;               
;
;******************************************************************************
;
; Usage -   Microsoft Visual C++ Compiler v5.0
;
;*****************************************************************************/

#if !defined(_FCRINST_H)
#define _FCRINST_H

#include "fcrapi.h"

#define	FCR_INST_SUCCESS					0x00000000
#define FCR_INST_FAILED_TO_READ_REFCOUNT	0x00000001
#define FCR_INST_FAILED_TO_SET_REFCOUNT		0x00000002
#define FCR_INST_FAILED_TO_GET_OS_VERSION	0x00000003
#define FCR_INST_FAILED_TO_GET_SYS_DIR		0x00000004
#define FCR_INST_FAILED_TO_OPEN_SCM			0x00000005
#define FCR_INST_FAILED_CREATE_SERVICE		0x00000006
#define FCR_INST_FAILED_TO_SET_REG_VALUE	0x00000007
#define FCR_INST_FAILED_TO_DELETE_REG_KEY	0x00000008
#define FCR_INST_FAILED_TO_OPEN_SERVICE		0x00000009
#define FCR_INST_FAILED_TO_DELETE_SERVICE	0x0000000A
#define FCR_INST_INVALID_CLIENT_ID			0x0000000b
#define FCR_INST_NAME_ALREADY_EXISTS		0x0000000C
#define FCR_INST_ID_ALREADY_EXISTS			0x0000000d

#define FCR_TLA					"CAFCR"
#define FCR_DISPLAY_NAME		"CA File Change Recorder"
#define FCR_EVENTLOG_KEY		"SYSTEM\\CurrentControlSet\\Services\\eventlog\\SYSTEM\\"FCR_TLA
#define FCR_PARAMETERS_KEY		"SYSTEM\\CurrentControlSet\\Services\\"FCR_TLA"\\Parameters"
#define FCR_SERVICE_KEY			"SYSTEM\\CurrentControlSet\\Services\\"FCR_TLA
#define	FCR_CLIENT_KEY			FCR_SERVICE_KEY"\\Client"

#if !defined _FCRINST_CPP
#if defined __cplusplus
extern "C"
{
#endif


_declspec(dllexport) DWORD	InstallFCR
(
	IN	LPSTR				lpDatabasePath,
	IN  FCR_CLIENTID_TYPE   clientID,
	IN  LPTSTR				clientName
);

_declspec(dllexport)  DWORD	UninstallFCR
(
	IN	FCR_CLIENTID_TYPE	clientID,
	OUT	PBOOL				pbDeleteAndReboot
);

_declspec(dllexport) BOOL	IsFCRInstalled
(
	VOID
);

#if defined __cplusplus
}
#endif
#endif


#endif	// _FCRINST_H