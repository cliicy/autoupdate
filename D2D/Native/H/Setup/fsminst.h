/*HEADER1.10*******************************************************************
; 
; © Copyright 2016 Arcserve, including its affiliates and subsidiaries.
; All rights reserved.  Any third party trademarks or copyrights are the
; property of their respective owners.
;
; Include file    - FSMinst.h
; Collection name - FSM
; Module name     - FSM installation
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

#if !defined(_FSMINST_H)
#define _FSMINST_H

#if defined __cplusplus
extern "C"
{
#endif

DWORD InstallFSM (IN BOOL bUpgrade)
/* bUpgrade specifies whether or not this is an upgrade.  FSM maintains a reference count which is incremented each time a new FSM client installs it. This allows the uninstall DLL to completely remove FSM only when the last client has finished with it. Setting the upgrade parameter to FALSE prevents the reference count from being incremented.  It is the responsibility of the calling application to know whether this is an upgrade or not and to set this parameter accordingly. */

DWORD UninstallFSM (PBOOL pbRebootRequired)
/*pbRebootRequired specifies whether or not a reboot is required. You can pass NULL in here if you don't need this information (normally when you already know that a reboot is required).*/

#if defined __cplusplus
}
#endif


#endif	// _FSMINST_H