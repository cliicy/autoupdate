/****************************************************************************
Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  Windows NT Tools
      Version:  3.10  Revision A
 Version Date:  January 10, 1994
****************************************************************************/

#ifndef _CSTOOL_H
#define _CSTOOL_H

#ifdef __cplusplus		   	
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */

#include <tchar.h>
#include <asvctl.h>

// chefr03
#include "bebenabled.h"

#ifndef PTCHAR
typedef TCHAR *PTCHAR;
#endif

#if     defined(UNICODE)
#define _TBYTES(s)      (lstrlen(s) * sizeof(TCHAR))
#define _TCHARS(s)      (sizeof(s) / sizeof(TCHAR))
#else
#define _TBYTES(s)      strlen(s)
#define _TCHARS(s)      sizeof(s)
#endif

#ifndef PFILETIME
typedef FILETIME *PFILETIME;
#endif

/*
 *      User Account
 */

#define _MAX_ACCT_NAME  64 
// Oripin: UNICODE_JIS Support
// BEGIN: UNICODE_JIS Support
struct  Cst_hostent {
	wchar_t    FAR * h_name;           /* official name of host */
	wchar_t    FAR * FAR * h_aliases;  /* alias list */
	short   h_addrtype;             /* host address type */
	short   h_length;               /* length of address */
	char    FAR * FAR * h_addr_list; /* list of addresses */
#define h_addr  h_addr_list[0]          /* address, for backward compat */
};
// End: UNICODE_JIS SUpport
// Oripin: UNICODE_JIS Support kalsa03
// BEGIN: UNICODE_JIS Support

struct Cst_servent {
	wchar_t FAR* s_name; 
	wchar_t FAR * FAR * s_aliases;
	short s_port;
	wchar_t FAR* s_proto;
};
// End: UNICODE_JIS SUpport

typedef struct  _CSI_ACCOUNTA {
  CHAR  domain[_MAX_ACCT_NAME],
        user[_MAX_ACCT_NAME],
        pw[_MAX_ACCT_NAME];
} CSI_ACCOUNTA, *PCSI_ACCOUNTA;

typedef struct  _CSI_ACCOUNTW {
  WCHAR domain[_MAX_ACCT_NAME],
        user[_MAX_ACCT_NAME],
        pw[_MAX_ACCT_NAME];
} CSI_ACCOUNTW, *PCSI_ACCOUNTW;

#if     defined(UNICODE)
typedef CSI_ACCOUNTW    CSI_ACCOUNT;
typedef PCSI_ACCOUNTW   PCSI_ACCOUNT;
#else
typedef CSI_ACCOUNTA    CSI_ACCOUNT;
typedef PCSI_ACCOUNTA   PCSI_ACCOUNT;
#endif

#if     (!defined(_CSTOOL_NOPROTOTYPE))

/*
 *      DOS Packed Date/Time
 */

USHORT WINAPI _dos_packdate(USHORT year, USHORT month, USHORT day);
USHORT WINAPI _dos_packtime(USHORT hour, USHORT minute, USHORT second);
void   WINAPI _dos_unpackdate(USHORT date, PUSHORT year, PUSHORT month, PUSHORT day);
void   WINAPI _dos_unpacktime(USHORT time, PUSHORT hour, PUSHORT minute, PUSHORT second);
DWORD  WINAPI FileTimeToPackedTime(PFILETIME);
BOOL   WINAPI PackedTimeToFileTime(DWORD, PFILETIME);

/*
 *      Memory
 */

PVOID WINAPI cstcalloc(UINT number, UINT size);
PVOID WINAPI cstfree(PVOID p);
PVOID WINAPI cstmalloc(UINT size);
PVOID WINAPI cstrealloc(void * _Memory, size_t _NewSize);

/*
 *      Strings
 */

#if     defined(UNICODE)
#define cstmemlen       cstmemlenw
#define cststrchr       cststrchrw
#define cststrdif       cststrdifw
#define cststrend       cststrendw
#define cststrfill      cststrfillw
#define cststrlen       cststrlenw
#define cststrrchr      cststrrchrw
#define cststrrev       cststrrevw
#define cststrrpl       cststrrplw
#define cststrstr       cststrstrw
#else
#define cstmemlen       cstmemlena
#define cststrchr       cststrchra
#define cststrdif       cststrdifa
#define cststrend       cststrenda
#define cststrfill      cststrfilla
#define cststrlen       cststrlena
#define cststrrchr      cststrrchra
#define cststrrev       cststrreva
#define cststrrpl       cststrrpla
#define cststrstr       cststrstra
#endif

UINT   WINAPI cstmemlen(PTCHAR start, PTCHAR end);
PTCHAR WINAPI cststrchr(PTCHAR string, TCHAR c);
PTCHAR WINAPI cststrdif(PTCHAR string1, PTCHAR string2);
PTCHAR WINAPI cststrend(PTCHAR string);
void   WINAPI cststrfill(PTCHAR string, TCHAR c, TCHAR count);
int    WINAPI cststrlen(PTCHAR string);
PTCHAR WINAPI cststrrchr(PTCHAR string, TCHAR c);
PTCHAR WINAPI cststrrev(PTCHAR string);
void   WINAPI cststrrpl(PTCHAR string, TCHAR c1, TCHAR c2);
PTCHAR WINAPI cststrstr(PTCHAR string1, PTCHAR string2);

/*
 *      Ansi / Unicode
 */

PWCHAR WINAPI AnsiToUnicode(PCHAR s, PWCHAR ws, UINT limit);
PCHAR  WINAPI UnicodeToAnsi(PWCHAR ws, PCHAR s, UINT limit);


// chefr03, Add below conversion functions for R12.V features:
// Password Management, Save Node Info and Role Management
//#if		defined(ARC_ROLE_MANAGEMENT) || defined(ARC_SAVE_NODEINFO) || defined(R12_5_MANAGE_PASSWORD)
DWORD	WINAPI	Utf8ToUnicode(	/* [IN] */		PCSTR	utf8Src,
								/* [IN] */		int		utf8SrcLen,		// -1 indicates utf8Src is NULL terminated
								/* [OUT] */		PWCHAR	unicodeDst,		// Can be NULL
								/* [IN/OUT] */	int*	unicodeDstLen);	// Return the needed length
DWORD	WINAPI	UnicodeToUtf8(	/* [IN] */		PCWSTR	unicodeSrc,
							  	/* [IN] */		int		unicodeSrcLen,	// -1 indicates unicodeSrc is NULL terminated
								/* [OUT] */		PCHAR	utf8Dst,		// Can be NULL
								/* [IN/OUT] */	int*	utf8DstLen);	// Return the needed length
//#endif


// chegu02, char set convert function  for R12.V  081204
// NOTE: AnsiToUTF8() and Utf8ToAnsi() Just Only for Auth Module!
DWORD	WINAPI	AnsiToUTF8( /* [IN] */		PCHAR szAnsiSrc,
						    /* [OUT] */		PCHAR szUtf8Dst,
							/* [IN] */		int nUtf8BufLen);
DWORD	WINAPI	Utf8ToAnsi( /* [IN] */		PCHAR szUtf8Src,
						    /* [IN] */		int utf8SrcLen, 
							/* [OUT] */		PCHAR szAnsiDst, 
							/* [IN] */		int nAnsiBufLen);


/*
 *      NetWare
 */

#if     defined(UNICODE)
#define _nw_attr_to_string _nw_attr_to_stringw
#define _nw_path_to_unc    _nw_path_to_uncw
#else
#define _nw_attr_to_string _nw_attr_to_stringa
#define _nw_path_to_unc    _nw_path_to_unca
#endif

void   WINAPI _nw_attr_to_string(PTCHAR string, ULONG attributes);
long   WINAPI _nw_longswap(long l);
void   WINAPI _nw_path_to_unc(PTCHAR path, USHORT limit);

/*
 *      International
 */

#if     defined(UNICODE)
#define _intl_reset     _intl_resetw
#define _intl_setup     _intl_setupw
#define _intl_dttos     _intl_dttosw
#define _intl_dttos4     _intl_dttos4w
#define _intl_etmtos    _intl_etmtosw
#define _intl_fatos     _intl_fatosw
#define _intl_fdttos    _intl_fdttosw
#define _intl_ftmtos    _intl_ftmtosw
#define _intl_ltos      _intl_ltosw
#define _intl_lltos     _intl_lltosw
#define _intl_kbtos     _intl_kbtosw
#define _intl_kbtos2    _intl_kbtos2w
#define _intl_dectos    _intl_dectosw
#define _intl_tmtos     _intl_tmtosw
#define _intl_default_setup     _intl_default_setupw
#define _intl_default_dttos     _intl_default_dttosw
#define _intl_default_tmtos     _intl_default_tmtosw
#else
#define _intl_reset     _intl_reseta
#define _intl_setup     _intl_setupa
#define _intl_dttos     _intl_dttosa
#define _intl_dttos4     _intl_dttos4a
#define _intl_etmtos    _intl_etmtosa
#define _intl_fatos     _intl_fatosa
#define _intl_fdttos    _intl_fdttosa
#define _intl_ftmtos    _intl_ftmtosa
#define _intl_ltos      _intl_ltosa
#define _intl_lltos     _intl_lltosa
#define _intl_kbtos     _intl_kbtosa
#define _intl_kbtos2    _intl_kbtos2a
#define _intl_dectos    _intl_dectosa
#define _intl_tmtos     _intl_tmtosa
#define _intl_default_setup     _intl_default_setupa
#define _intl_default_dttos     _intl_default_dttosa
#define _intl_default_tmtos     _intl_default_tmtosa
#endif

USHORT WINAPI _intl_reset(PTCHAR section);
USHORT WINAPI _intl_setup(void);

PTCHAR WINAPI _intl_dectos(PTCHAR string, ULONG value);
PTCHAR WINAPI _intl_dttos(PTCHAR string, short year, short month, short day);
PTCHAR WINAPI _intl_dttos4(PTCHAR string, short year, short month, short day);
PTCHAR WINAPI _intl_etmtos(PTCHAR string, ULONG elapesTime);
PTCHAR WINAPI _intl_fatos(PTCHAR string, ULONG attr);
PTCHAR WINAPI _intl_fdttos(PTCHAR string, USHORT date);
PTCHAR WINAPI _intl_ftmtos(PTCHAR string, USHORT time);
PTCHAR WINAPI _intl_ltos(PTCHAR string, ULONG value);
PTCHAR WINAPI _intl_lltos(PTCHAR string, ULONG hivalue, ULONG lovalue);
PTCHAR WINAPI _intl_kbtos(PTCHAR string, ULONG kb);
PTCHAR WINAPI _intl_kbtos2(PTCHAR string, ULONG kb);
PTCHAR WINAPI _intl_tmtos(PTCHAR string, short hour, short minute, short second);

USHORT WINAPI _intl_default_setup(void);
PTCHAR WINAPI _intl_default_dttos(PTCHAR string, short year, short month, short day);
PTCHAR WINAPI _intl_default_tmtos(PTCHAR string, short hour, short minute, short second);

/*
 *      System & User Settings
 */

#if     defined(UNICODE)
#define CstDelSetting       CstDelSettingW
#define CstGetSettingBinary CstGetSettingBinaryW
#define CstGetSettingInt    CstGetSettingIntW
#define CstGetSettingString CstGetSettingStringW
#define CstSetSettingBinary CstSetSettingBinaryW
#define CstSetSettingInt    CstSetSettingIntW
#define CstSetSettingString CstSetSettingStringW
#define CstDelUser          CstDelUserW
#define CstGetUserBinary    CstGetUserBinaryW
#define CstGetUserInt       CstGetUserIntW
#define CstGetUserString    CstGetUserStringW
#define CstSetUserBinary    CstSetUserBinaryW
#define CstSetUserInt       CstSetUserIntW
#define CstSetUserString    CstSetUserStringW
#define	CstSetSettingLong	CstSetSettingLongW
#define	CstGetSettingLong	CstGetSettingLongW
#define	CstGetCheetahSettingString	CstGetCheetahSettingStringW
#define	CstGetASITSettingString		CstGetASITSettingStringW

#else
#define CstDelSetting       CstDelSettingA
#define CstGetSettingBinary CstGetSettingBinaryA
#define CstGetSettingInt    CstGetSettingIntA
#define CstGetSettingString CstGetSettingStringA
#define CstSetSettingBinary CstSetSettingBinaryA
#define CstSetSettingInt    CstSetSettingIntA
#define CstSetSettingString CstSetSettingStringA
#define CstDelUser          CstDelUserA
#define CstGetUserBinary    CstGetUserBinaryA
#define CstGetUserInt       CstGetUserIntA
#define CstGetUserString    CstGetUserStringA
#define CstSetUserBinary    CstSetUserBinaryA
#define CstSetUserInt       CstSetUserIntA
#define CstSetUserString    CstSetUserStringA
#define	CstSetSettingLong	CstSetSettingLongA
#define	CstGetSettingLong	CstGetSettingLongA
#define	CstGetCheetahSettingString	CstGetCheetahSettingStringA
#define	CstGetASITSettingString		CstGetASITSettingStringA


#endif

UINT   WINAPI CstDelSetting(PTCHAR serverName, PTCHAR section, 
                            PTCHAR subSection, PTCHAR key);
UINT   WINAPI CstGetSettingBinary(PTCHAR serverName, PTCHAR section, 
                                  PTCHAR subSection, PTCHAR key, 
                                  PVOID defaultBuffer, PVOID buffer, 
                                  ULONG bufferSize);
UINT   WINAPI CstGetSettingInt(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               UINT defaultValue);

ULONG   WINAPI CstGetSettingLong(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               ULONG defaultValue);

UINT   WINAPI CstGetSettingString(PTCHAR serverName, PTCHAR section, 
                                  PTCHAR subSection, PTCHAR key, 
                                  PTCHAR defaultString, PTCHAR buffer, 
                                  ULONG bufferSize);

UINT   WINAPI CstGetCheetahSettingString(PTCHAR serverName, PTCHAR section, 
                                  PTCHAR subSection, PTCHAR key, 
                                  PTCHAR defaultString, PTCHAR buffer, 
                                  ULONG bufferSize);

UINT   WINAPI CstGetASITSettingString(PTCHAR serverName, PTCHAR section, 
                                  PTCHAR subSection, PTCHAR key, 
                                  PTCHAR defaultString, PTCHAR buffer, 
                                  ULONG bufferSize);


UINT   WINAPI CstSetSettingBinary(PTCHAR serverName, PTCHAR section, 
                                  PTCHAR subSection, PTCHAR key, 
                                  PVOID buffer, ULONG bufferSize);
UINT   WINAPI CstSetSettingInt(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               UINT value);
ULONG   WINAPI CstSetSettingLong(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               ULONG value);

UINT   WINAPI CstSetSettingString(PTCHAR serverName, PTCHAR section, 
                                  PTCHAR subSection, PTCHAR key, 
                                  PTCHAR string);
UINT   WINAPI CstDelUser(PTCHAR serverName, PTCHAR section, 
                         PTCHAR subSection, PTCHAR key);
UINT   WINAPI CstGetUserBinary(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               PVOID defaultBuffer, PVOID buffer, 
                               ULONG bufferSize);
UINT   WINAPI CstGetUserInt(PTCHAR serverName, PTCHAR section, 
                            PTCHAR subSection, PTCHAR key, 
                            UINT defaultValue);
UINT   WINAPI CstGetUserString(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               PTCHAR defaultString, PTCHAR buffer, 
                               ULONG bufferSize);
UINT   WINAPI CstSetUserBinary(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               PVOID buffer, ULONG bufferSize);
UINT   WINAPI CstSetUserInt(PTCHAR serverName, PTCHAR section, 
                            PTCHAR subSection, PTCHAR key, 
                            UINT value);
UINT   WINAPI CstSetUserString(PTCHAR serverName, PTCHAR section, 
                               PTCHAR subSection, PTCHAR key, 
                               PTCHAR string);

/*
 *      Cheyenne Setup Specifics
 */

#if     defined(UNICODE)
#define CstGetDatabaseDirectory CstGetDatabaseDirectoryW
#define CstGetHomeDirectory     CstGetHomeDirectoryW
#define CstGetHostServer        CstGetHostServerW
#define CstGetLogDirectory      CstGetLogDirectoryW
#define CstGetProgramDirectory  CstGetProgramDirectoryW
#define CstGetRegistryDirectory CstGetRegistryDirectoryW
#define CstGetString            CstGetStringW
#define CstGetSystemDirectory   CstGetSystemDirectoryW
#define CstGetTempDirectory     CstGetTempDirectoryW
#define CstGetUserDirectory     CstGetUserDirectoryW
#define CstGetUserName          CstGetUserNameW
#define CstGetUserAccount       CstGetUserAccountW
#define CstSetUserAccount       CstSetUserAccountW
#define CstGetSQLAccount        CstGetSQLAccountW
#define CstSetSQLAccount        CstSetSQLAccountW
#define CstGetCheetahSQLAccount     CstGetCheetahSQLAccountW
#define CstGetASITSQLAccount        CstGetASITSQLAccountW
#define CstGetRemoteSQLAccount        CstGetRemoteSQLAccountW
#define CstSetRemoteSQLAccount        CstSetRemoteSQLAccountW
#define CstGetIngresAccount     CstGetIngresAccountW
#define CstSetIngresAccount     CstSetIngresAccountW
#define CstGetRegBaseString		CstGetRegBaseStringW
#define CstGetRegRootString		CstGetRegRootStringW
#define CstGetDiscoveryTblString	CstGetDiscoveryTblStringW
#define CstGetProductsString	CstGetProductsStringW
#define CstGetBrandNameString	CstGetBrandNameStringW
#define	CstGetRootKeyName		CstGetRootKeyNameW
#define	CstGetRootKeyNameEx		CstGetRootKeyNameExW

#else
#define CstGetDatabaseDirectory CstGetDatabaseDirectoryA
#define CstGetHomeDirectory     CstGetHomeDirectoryA
#define CstGetHostServer        CstGetHostServerA
#define CstGetLogDirectory      CstGetLogDirectoryA
#define CstGetProgramDirectory  CstGetProgramDirectoryA
#define CstGetRegistryDirectory CstGetRegistryDirectoryA
#define CstGetString            CstGetStringA
#define CstGetSystemDirectory   CstGetSystemDirectoryA
#define CstGetTempDirectory     CstGetTempDirectoryA
#define CstGetUserDirectory     CstGetUserDirectoryA
#define CstGetUserName          CstGetUserNameA
#define CstGetUserAccount       CstGetUserAccountA
#define CstSetUserAccount       CstSetUserAccountA
#define CstGetSQLAccount        CstGetSQLAccountA
#define CstSetSQLAccount        CstSetSQLAccountA
#define CstGetCheetahSQLAccount     CstGetCheetahSQLAccountA
#define CstGetASITSQLAccount        CstGetASITSQLAccountA
#define CstGetRemoteSQLAccount        CstGetRemoteSQLAccountA
#define CstSetRemoteSQLAccount        CstSetRemoteSQLAccountA
#define CstGetIngresAccount     CstGetIngresAccountA
#define CstSetIngresAccount     CstSetIngresAccountA

#define CstGetRegBaseString		CstGetRegBaseStringA
#define CstGetRegRootString		CstGetRegRootStringA
#define CstGetDiscoveryTblString		CstGetDiscoveryTblStringA
#define	CstGetProductsString	CstGetProductsStringA
#define CstGetBrandNameString	CstGetBrandNameStringA
#define	CstGetRootKeyName		CstGetRootKeyNameA
#define	CstGetRootKeyNameEx		CstGetRootKeyNameExA
#endif

USHORT WINAPI CstGetDatabaseDirectory(PTCHAR serverName, PTCHAR path, 
                                      USHORT limit);
USHORT WINAPI CstGetHomeDirectory(PTCHAR serverName, PTCHAR path, 
                                  USHORT limit);
USHORT WINAPI CstGetHostServer(PTCHAR serverName);
USHORT WINAPI CstGetLogDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI CstGetProgramDirectory(PTCHAR path, USHORT limit);
USHORT WINAPI CstGetRegistryDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI CstGetString(USHORT id, PTCHAR name, USHORT limit);
USHORT WINAPI CstGetSystemDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI CstGetTempDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI CstGetUserDirectory(PTCHAR serverName, PTCHAR path, USHORT limit);
USHORT WINAPI CstGetUserName(PTCHAR serverName, PTCHAR name, USHORT limit);
BOOL   WINAPI CstGetUserAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstSetUserAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstGetSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstSetSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstGetRemoteSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstSetRemoteSQLAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstGetIngresAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstSetIngresAccount(PTCHAR serverName, PCSI_ACCOUNT account);
BOOL   WINAPI CstGetCheetahSQLAccount(PTCHAR serverName, PCSI_ACCOUNT pAccount);
BOOL   WINAPI CstGetASITSQLAccount(PTCHAR serverName, PCSI_ACCOUNT pAccount);

//----------------------------------------------------------------------------------------------------
//	Synopsys: These API's return path to different registry locations
//
//	Return value: number of bytes (ANSI) or chararcters (UNICODE) copied into buffer (name)
//
//	Parameters: 
//		 name	-  points to buffer to receive string
//		 limit 	-  buffer size (bytes or characters). The string will be truncated and null terminated 
//					if it's longer than size specified
//-----------------------------------------------------------------------------------------------------

USHORT WINAPI CstGetRegBaseString(PTCHAR name, USHORT limit);
USHORT WINAPI CstGetRegRootString(PTCHAR name, USHORT limit);
USHORT WINAPI CstGetDiscoveryTblString(PTCHAR name, USHORT limit);
USHORT WINAPI CstGetProductsString(PTCHAR name, USHORT limit);
USHORT WINAPI CstGetBrandNameString(PTCHAR name, USHORT limit);
LPTSTR WINAPI CstGetRootKeyName();

/*
 *      Errors
 */

#if     defined(UNICODE)
#define CstGetErrorText CstGetErrorTextW
#else
#define CstGetErrorText CstGetErrorTextA
#endif

USHORT WINAPI CstGetErrorText(ULONG errorCode, PTCHAR text, USHORT limit);

/*
 *      Message Box
 */
// linku02 9/28/04 For privilege-elevation issue, implement CstMsgBox by invoking a GUI process
// that displays the original message box whihc is now implemented in CstMsgBoxOriginal. 

#define CST_MAX_PATH 1024
#if     defined(UNICODE)
#define CstMsgBox         CstMsgBoxW
#define CstMsgBoxOriginal CstMsgBoxOriginalW
#define CstSvcExecCmdLine CstSvcExecCmdLineW
#define CstSvcMsgBox      CstSvcMsgBoxW
#define CstSvcMsgBoxEx	  CstSvcMsgBoxExW	
#define CstGetTaskListNT  CstGetTaskListNTW
#define CstAddUIMachineRef	CstAddUIMachineRefW
#define CstReleaseUIMachineRef	CstReleaseUIMachineRefW
#define CstEnumUIMachineOpen	CstEnumUIMachineOpenW
#define CstEnumUIMachineNext	CstEnumUIMachineNextW
#define CstEnumUIMachineHasMore	CstEnumUIMachineHasMoreW
#define CstEnumUIMachineClose	CstEnumUIMachineCloseW
#define CstSetSessionPrimary	CstSetSessionPrimaryW
#define CstRemoveSessionPrimary	CstRemoveSessionPrimaryW
#define CstIsSessionPrimaryAllowed CstIsSessionPrimaryAllowedW
#define CstIsSenderFromPrimaryAllowed CstIsSenderFromPrimaryAllowedW
#define CstIsProcessAlive CstIsProcessAliveW
#define CstGUIAliveInSession CstGUIAliveInSessionW
#define CstGetCurrentSessionID CstGetCurrentSessionIDW
#else
#define CstMsgBox         CstMsgBoxA
#define CstMsgBoxOriginal CstMsgBoxOriginalA
#define CstSvcExecCmdLine CstSvcExecCmdLineA
#define CstSvcMsgBox      CstSvcMsgBoxA
#define CstSvcMsgBoxEx	  CstSvcMsgBoxExA	
#define CstGetTaskListNT  CstGetTaskListNTA
#define CstAddUIMachineRef	CstAddUIMachineRefA
#define CstReleaseUIMachineRef	CstReleaseUIMachineRefA
#define CstEnumUIMachineOpen	CstEnumUIMachineOpenA
#define CstEnumUIMachineNext	CstEnumUIMachineNextA
#define CstEnumUIMachineHasMore	CstEnumUIMachineHasMoreA
#define CstEnumUIMachineClose	CstEnumUIMachineCloseA
#define CstSetSessionPrimary	CstSetSessionPrimaryA
#define CstRemoveSessionPrimary	CstRemoveSessionPrimaryA
#define CstIsSessionPrimaryAllowed CstIsSessionPrimaryAllowedA
#define CstIsSenderFromPrimaryAllowed CstIsSenderFromPrimaryAllowedA
#define CstIsProcessAlive CstIsProcessAliveA
#define CstGUIAliveInSession CstGUIAliveInSessionA
#define CstGetCurrentSessionID CstGetCurrentSessionIDA
#endif

int     CstMsgBox(HWND hwndOwner, PTCHAR text, PTCHAR title, UINT style,
                  UINT timeout, UINT defID);
int     CstMsgBoxOriginal(HWND hwndOwner, PTCHAR text, PTCHAR title, UINT style,
                  UINT timeout, UINT defID);
int     CstSvcExecCmdLine(PTCHAR pszCmdLine);
int     CstSvcMsgBox(PTCHAR pszText, PTCHAR pszTitle, UINT fuStyle, 
                     ULONG ulTimeout, UINT uiDefID, BOOL bBroadcast);

int     CstSvcMsgBoxEx(PTCHAR pszText, PTCHAR pszTitle, UINT fuStyle, 
                     ULONG ulTimeout, UINT uiDefID, BOOL bBroadcast, PTCHAR pszPipe,
					 ULONG ulWaitForPipeInstance, ULONG ulMaxRetry, ULONG ulWaitInterval);

DWORD   CstAddUIMachineRef(PTCHAR pszServer);
DWORD	CstReleaseUIMachineRef(PTCHAR pszServer);
PVOID	CstEnumUIMachineOpen();
DWORD   CstEnumUIMachineNext(PVOID pHandle, PTCHAR pszMachine, DWORD dwMachineLength);
BOOL	CstEnumUIMachineHasMore(PVOID pHandle);
VOID	CstEnumUIMachineClose(PVOID pHandle);

DWORD	CstSetSessionPrimary(PTCHAR pszServer, DWORD dwServerLength);
DWORD	CstRemoveSessionPrimary(INT nSessionID);
BOOL	CstIsSessionPrimaryAllowed(DWORD dwSessionID, PTCHAR pszMachine);
BOOL	CstIsSenderFromPrimaryAllowed(PTCHAR pszMachine);
BOOL	CstIsProcessAlive(PTCHAR pszProcessName);
INT		CstGUIAliveInSession(DWORD dwSessionID);
DWORD	CstGetCurrentSessionID();

/*
 *      Network
 */

#define _CST_NET_DEF_SHARE      0x80000000
#define _CST_NET_USER_SHARE     0x00000000

#if     defined(UNICODE)
#define CstNetShareClose CstNetShareCloseW
#define CstNetShareEnum  CstNetShareEnumW
#define CstNetShareEnum2 CstNetShareEnum2W
#define CstNetShareOpen  CstNetShareOpenW
#define CstWNetAddConnection2 CstWNetAddConnection2W
#define CstWNetCancelConnection2 CstWNetCancelConnection2W
#define CstNetGetDCName	CstNetGetDCNameW
#define	CstNetWkstaGetInfo CstNetWkstaGetInfoW
#define CstNetServerGetInfo CstNetServerGetInfoW
#define CstNetApiBufferFree CstNetApiBufferFreeW
#define CstNetShareGetInfo CstNetShareGetInfoW

#else
#define CstNetShareClose CstNetShareCloseA
#define CstNetShareEnum  CstNetShareEnumA
#define CstNetShareEnum2 CstNetShareEnum2A
#define CstNetShareOpen  CstNetShareOpenA
#define CstWNetAddConnection2 CstWNetAddConnection2A
#define CstWNetCancelConnection2 CstWNetCancelConnection2A
#define CstNetGetDCName	CstNetGetDCNameA
#define CstNetWkstaGetInfo CstNetWkstaGetInfoA
#define CstNetServerGetInfo CstNetServerGetInfoA
#define CstNetApiBufferFree CstNetApiBufferFreeA
#define CstNetShareGetInfo CstNetShareGetInfoA
#endif

UINT  WINAPI CstNetShareClose(PVOID enumPtr);
UINT  WINAPI CstNetShareEnum(PVOID enumPtr, PULONG type, PTCHAR name, 
                             UINT limit);
UINT  WINAPI CstNetShareEnum2(PVOID enumPtr, PULONG type, 
                              PTCHAR name, UINT nameLimit,
                              PTCHAR desc, UINT descLimit);
PVOID WINAPI CstNetShareOpen(PTCHAR serverName, PULONG retCode);
DWORD CstWNetAddConnection2(LPNETRESOURCE lpNetResource,LPCTSTR lpPassword,LPCTSTR lpUsername,DWORD dwFlags,HANDLE *pHANDLE);
DWORD CstWNetCancelConnection2(HANDLE Handle, LPTSTR lpName, DWORD dwFlags, BOOL fForce);
DWORD  WINAPI CstNetGetDCName(
  LPCWSTR servername,  
  LPCWSTR domainname,  
  LPBYTE *bufptr       
);

DWORD  WINAPI CstNetWkstaGetInfo(
  LPWSTR servername,  
  DWORD level,        
  LPBYTE *bufptr      
);

DWORD  WINAPI CstNetServerGetInfo(
  LPWSTR servername,  
  DWORD level,        
  LPBYTE *bufptr      
);

DWORD  WINAPI CstNetApiBufferFree(
  LPVOID Buffer  
);

DWORD  WINAPI  CstNetShareGetInfo(
  LPWSTR servername,  
  LPWSTR netname,     
  DWORD level,        
  LPBYTE *bufptr      
);

/*
 *      Misc.
 */

#if     defined(UNICODE)
#define FileExists     FileExistsW
#define IsRemoteServer IsRemoteServerW
#else
#define FileExists     FileExistsA
#define IsRemoteServer IsRemoteServerA
#endif

BOOL   WINAPI FileExists(PTCHAR path); 
BOOL   WINAPI IsThreadActive(HANDLE hThread);
BOOL   WINAPI IsRemoteServer(PTCHAR serverName);

#define TITLE_SIZE          64
#define PROCESS_SIZE        16
typedef struct _TASK_LIST
{
    DWORD       dwProcessId;
    DWORD       dwInheritedFromProcessId;
    BOOL        flags;
    HANDLE      hwnd;
    TCHAR       ProcessName[PROCESS_SIZE];
    TCHAR       WindowTitle[TITLE_SIZE];
} TASK_LIST, *PTASK_LIST;

typedef struct _TASK_LIST_ENUM
{
    PTASK_LIST  tlist;
    DWORD       numtasks;
} TASK_LIST_ENUM, *PTASK_LIST_ENUM;

//////////////////////////////////////////////////////////////////////////////////////
// GetTaskList	- Builds a list with all Running Applications						//
//----------------------------------------------------------------------------------//
// dwNumTasks	<- maximum number of tasks that the pTask array can hold			//
// Return		-     Number of tasks placed into the pTask array.					//
//////////////////////////////////////////////////////////////////////////////////////
#if     defined(UNICODE)
#define	 CstGetTaskList CstGetTaskListW
#else
#define	 CstGetTaskList CstGetTaskListA
#endif

DWORD	 WINAPI CstGetTaskList( PTASK_LIST pTask, DWORD dwNumTasks );

//////////////////////////////////////////////////////////////////////////////////////
//	Functions for registering and unregistering running applications				//
//////////////////////////////////////////////////////////////////////////////////////

typedef enum _APPTYPE		// Declare enum type APPTYPE
{
   CheyWinApp,				// Windows Application
   CheyService,				// Service
   CheyBkgApp				// Background Application
} APPTYPE;

typedef void (*FUNCPTR)(LPVOID);	// Pointer to a function

#if     defined(UNICODE)
#define CheyRegisterForStop     CheyRegisterForStopW
#define CheyUnRegisterForStop	CheyUnRegisterForStopW
#define CheyStopApps			CheyStopAppsW
#define CheyForceStopApps		CheyForceStopAppsW
#define CstGetTempFileName		CstGetTempFileNameW
#define IsWindowsVersionOK		IsWindowsVersionOKW
#define	CstIsUserAdmin			CstIsUserAdminW
#else
#define CheyRegisterForStop     CheyRegisterForStopA
#define CheyUnRegisterForStop	CheyUnRegisterForStopA
#define CheyStopApps			CheyStopAppsA
#define CheyForceStopApps		CheyForceStopAppsA
#define CstGetTempFileName		CstGetTempFileNameA
#define IsWindowsVersionOK		IsWindowsVersionOKA
#define	CstIsUserAdmin			CstIsUserAdminA
#endif

DWORD WINAPI CheyRegisterForStop( APPTYPE AppType, PTCHAR ptcAppName, FUNCPTR funcptr, LPVOID pfuncarg);
DWORD WINAPI CheyUnRegisterForStop( PTCHAR ptcAppName );
DWORD WINAPI CheyStopApps( USHORT usTimeOut, DWORD* dwNrOfRunningApps );
DWORD WINAPI CheyForceStopApps( USHORT usTimeOut );
// try to build a name for a tmp file; if OK returns TRUE
BOOL  WINAPI CstGetTempFileName(LPCTSTR lpPathName,		// address of directory name for temporary file 
								LPCTSTR lpPrefixString, // address of filename prefix (only first 3 char count)
								UINT	uUnique,		// if != 0 the number is added to the prefix
														// else the name is generate from the result returned by "time" function
 								LPTSTR	lpTempFileName); // address of buffer that receives the new filename 
//
// Verify Windows version requirements
//
BOOL WINAPI  IsWindowsVersionOK(DWORD dwMajor, DWORD dwMinor, DWORD dwSPMajor );

// Checks the current user administrator rights
BOOL WINAPI CstIsUserAdmin();

// this function checks if SERVER component is installed on local machine
BOOL WINAPI IsServerInstalled();

/////////////////////////////////////////////////////////////////////////////////
struct hostent * Cst_gethostbyname(const char *hostname);
// Oripin: UNICODE_JIS Support kalsa03 retained above function and added below three
struct Cst_hostent * Cst_gethostbynameW(const wchar_t *hostname);
struct Cst_hostent* Cst_gethostbyaddr(const wchar_t* addr, int len, int type);
BOOL IsClusterServiceRunning(BOOL *pbFirst);
DWORD FindResourceTypeOnVServerByDependency(WCHAR *pVServerAddress, WCHAR *pVServerName, WCHAR *pResourceType, BOOL *hasResourceType);
DWORD GetDiskNamesOnClusterVServer(WCHAR *pVServerAddress, WCHAR *pDiskNameBuff, DWORD *pSize);
int Cst_getLocalHostName(unsigned char *localHostName, int len);
struct hostent * Cst_gethostbyname_WithNoClusterIP(const char *hostname);
struct hostent * Cst_gethostbyname_WithClusterIP(const char *hostname);



//A bad coding style by delcaring interface with TCHAR in DLL
//replace with Cst_getBABHostNameW/A and Cst_getClusterVirtualNodeNameW/A
#if     defined(UNICODE)
#define Cst_getClusterVirtualNodeName Cst_getClusterVirtualNodeNameW
#define Cst_getBABHostName Cst_getBABHostNameW
#else
#define Cst_getClusterVirtualNodeName Cst_getClusterVirtualNodeNameA
#define Cst_getBABHostName	Cst_getBABHostNameA
#endif 

LONG Cst_getClusterVirtualNodeNameW(WCHAR *szBuffer, DWORD dwBufLenInWCHAR);
LONG Cst_getClusterVirtualNodeNameA(CHAR  *szBuffer, DWORD dwBufLenInCHAR);

LONG Cst_getBABHostNameW( WCHAR *szBuffer, DWORD dwBufLenInWCHAR );
LONG Cst_getBABHostNameA( CHAR *szBuffer, DWORD dwBufLenInCHAR );


int WINAPI GetAgentsEqualToWriter(char *writer, char *agentNames, int *size);
int WINAPI GetAgentsEqualToWriterW(wchar_t *writer, wchar_t *agentNames, int *size);

/*
 * Data structures definitions and functions 
 * used for cluster-aware installation
 */

#define CLUS_MAX_NAME_LEN     512 //must greater than MAX_PATH
#define CLUS_MAX_IP_LEN       16

#define CLUS_MAX_DRIVE_NUM    26

#define CLUS_PROPBUFF_SIZE    10240

#define CLUS_PROP_RES_QUORUM  0x00000001

#define CLUS_PROP_NOD_AVAIL   0x00000001
#define CLUS_PROP_NOD_CONFED  0x00000002

#define CLUS_PROP_GRP_BAB     0x00000001
#define CLUS_PROP_GRP_SQL     0x00000002

#define CLUS_REGKEY_BASE      CST_REG_ROOT_L L"\\Base"
#define CLUS_REGKEY_PRODUCT   CST_REG_ROOT_L L"\\ProductsConfigInfo"
#define CLUS_REGKEY_ELODS     CST_REG_ROOT_L L"\\ELODistributedServers"

BOOL IsClusteredInstallationSupported();
BOOL IsBackupServerClusterAware();

/**************************************
 *
 * Used to collect groups' infomation
 *
 **************************************/

typedef struct _CLUSIPAINFO {
	UINT        uiAddress;
	UINT        uiSubnetMask;
	WCHAR       szNetwork[CLUS_MAX_NAME_LEN];
} CLUSIPAINFO, *PCLUSIPAINFO;

typedef struct _CLUSPHDINFO {
	WCHAR       szRescName[CLUS_MAX_NAME_LEN + 1];
	WCHAR       szDrvLetter[CLUS_MAX_DRIVE_NUM];
	DWORD       dwProp;
} CLUSPHDINFO, *PCLUSPHDINFO;

typedef struct _CLUSGRPINFO {
	WCHAR       szGroupName[CLUS_MAX_NAME_LEN];
	WCHAR       szVCompName[CLUS_MAX_NAME_LEN];
    //added by wu$pu01 for HA_SUPPORT
    WCHAR       szVNETResName[CLUS_MAX_NAME_LEN];
	CLUSIPAINFO sIPAddress;
	CLUSPHDINFO szPhysDisk[CLUS_MAX_DRIVE_NUM];
	DWORD       dwProp;
} CLUSGRPINFO, *PCLUSGRPINFO;

typedef PVOID HGRPENUM;

HGRPENUM     OpenClusGroupEnum();
PCLUSGRPINFO ClusGroupEnum(HGRPENUM hEnum,DWORD dwIndex);
DWORD        CloseClusGroupEnum(HGRPENUM hEnum);

/**************************************
 *
 * Used to collect networks' infomation
 *
 **************************************/

typedef struct _CLUSNTWINFO {
	WCHAR       szNetwName[CLUS_MAX_NAME_LEN];
	UINT        uiAddress;
	UINT        uiAddressMask;
} CLUSNTWINFO, *PCLUSNTWINFO;

typedef PVOID HNTWENUM;

HNTWENUM     OpenClusNetworkEnum();
PCLUSNTWINFO ClusNetworkEnum(HNTWENUM hEnum,DWORD dwIndex);
DWORD        CloseClusNetworkEnum(HNTWENUM hEnum);

/**************************************
 *
 * Used to collect nodes' infomation
 *
 **************************************/

typedef struct _CLUSNODINFO {
	WCHAR       szNodeName[CLUS_MAX_NAME_LEN];
	DWORD       dwProp;
} CLUSNODINFO, *PCLUSNODINFO;

typedef PVOID HNODENUM;

HNTWENUM     OpenClusNodeEnum(LPWSTR lpszDiskName);
PCLUSNODINFO ClusNodeEnum(HNODENUM hEnum,DWORD dwIndex);
DWORD        CloseClusNodeEnum(HNODENUM hEnum);

/**************************************
 *
 * Used to configure cluster group
 * and create cluster resources
 *
 **************************************/

DWORD PreConfigureCluster(LPWSTR lpszGroup,LPWSTR lpszDisk,UINT uiAddress,
						  UINT uiSubnetMask,LPWSTR lpszNetwork,LPWSTR lpszName);
DWORD ConfigureCluster(LPWSTR lpszGroup,
					   LPWSTR lpszPath,LPWSTR lpszShareName,LPWSTR *lppszNodes);

/**************************************
 *
 * Used to clean cluster configuration
 * and delete cluster resources
 *
 **************************************/

DWORD CleanCluster();

/**************************************
 *
 * Used to move a group to a node
 *
 **************************************/
DWORD MoveGroup(LPWSTR lpszGroup,LPWSTR lpszNode);

/***********************************************
* added by wu$pu01 to support cluster-aware setup for HA_SETUP
*	Cst_getHAPlatform								@1481
*	Cst_isStartFromFailover							@1482
*	Cst_verifyHostnameForSetup						@1483
*	Cst_verifyInstallPathForSetup					@1484
*	Cst_HAPostSetup									@1485
*
************************************************/
//constants for Cst_getHAPlatform
#define HA_UNKNOWN      0x0        //non-cluster 
#define HA_WANSYNC	   0x1        //for WANSYNC cluster
#define HA_MSCS		   0x2        //for MSCS cluster 
#define HA_NEC		      0x4        //for NEC cluster

#define HA_PSINSTALL    0x1        //primary server install
#define HA_MSINSTALL    0x2        //member server install
#define HA_MASTERINSTALL   0x4     //WANSYNC master node install
#define HA_REPLICAINSTALL  0x8     //WANSYNC replica node install

//constants for Cst_verifyHostnameForSetup 
#define HAOK_ISMASTER	 11   //pass "master node" verification 
#define HAOK_ISREPLICA	 12   //pass "replica node" verification
#define HAERR_INVALID_IP 13   //the input hostname can't be resolved as valid IP
#define HAERR_NO_HASVR	 14   //can't detect HA service running in target machine
#define HAERR_ISREALNAME 15   //the input hostname is a physical machien name

//constants for Cst_verifyInstallPathForSetup
#define HAOK_NOERR			0  //OK
#define HAERR_NO_DISKRES	21 //verfication failed because the inputted path is a local path
#define HAERR_NO_NETRES		22 //verfication failed due to network error 
#define HAERR_UNSUPPORT		23 //verification failed due to unknown error

//constants for Cst_getHASetting
#define HAERR_BABHOST      31
#define HAERR_NONCLUSTER   32

//constant for Cst_isStartFromFailover
#define HA_JE	0              //service type for Job engine
//HA control codes
#define HACTL_CREATERES    0x1 //create cluster resource for MSCS or NEC

#define CLUS_MAX_HOSTNAME 64

//shared data structure for setup, bconfig.exe cluster modules
typedef struct _BABHASETUPCFG{
	DWORD dwHAType;                           //setup will set its value to indicate the HA platform
	WCHAR szInstallPath[CLUS_MAX_NAME_LEN];   //the BAB install path
	WCHAR szVHostname[CLUS_MAX_HOSTNAME];     //virtual server name 
	WCHAR szVIPAddr[CLUS_MAX_IP_LEN];         //virtual IP address
   WCHAR szVIPMask[CLUS_MAX_IP_LEN];         //virtual net mask
   WCHAR szMasterNode[CLUS_MAX_HOSTNAME];    //master node name
   WCHAR szMasterIP[CLUS_MAX_IP_LEN];        //master node IP address
	WCHAR szReplicaNode[CLUS_MAX_HOSTNAME];   //replica node name
	WCHAR szReplicaIP[CLUS_MAX_IP_LEN];       //replica node IP address
   DWORD dwASDBType;					            //ASDB type:
   WCHAR szASDBIns[CLUS_MAX_HOSTNAME];       //ASDB instance name
	WCHAR szAsdbPath[CLUS_MAX_NAME_LEN];    	//ASDB data path 
   DWORD dwInstallType;                      //BAB install type: PrimaryServer or Member Server
   DWORD dwClusterNodeType;                  //Cluster Node type: 1 for master, other for replica
   DWORD dwHACtrlFlags;                      //flags for HA setup, HACTL_CREATERES(0x1) for creating HA related resource
   UCHAR reserved[504];                      //reserved 
}BABHASETUPCFG,*PBABHASETUPCFG;

// =============================================================================================
/// -function: Cst_getHAPlatform
///		detect underlying HA platform 
/// @return value: 
///		HA_UNKNOWN -- unsupported HA platform, do normal installation
///		HA_WANSYNC	--- for WANSYNC HA 
///		HA_MSCS		--- for MSCS HA
///		HA_NEC		--- for NEC cluster
/// @parameters: none
// ============================================================================================== 
DWORD Cst_getHAPlatform();

// ===============================================================================================
/// -check if a failover happened,SvrType is reserved for extension, current supported iSvrType is 
/// HA_JE
///
/// @return value:
///   true  --- the machine is just rebooting due to a "failover" event
///   false --- it is a normal reboot 
/// @parameters: 
///   @iSvrType [int], in case of multiple service care "failover" event caller should 
///   pass iSvrType as a identification for itself, current implementation only support
///   jobengine         
// ===============================================================================================
BOOL  Cst_isStartFromFailover(int iSvrType);

// ===============================================================================================
/// -function: Cst_verifyHostnameForSetup
///      just apply for WANSyncHA, check if the hostname is a valid master node or replica node
/// @return value: 
///      HAOK_ISMASTER, it is a valid master node 
///      HAOK_ISREPLICA,it is a valid replica node
///      HAERR_INVALID_IP, can't reslove hostname into IP 
///      HAERR_NO_HASVR, can't detect WANSYNC HA service running in hostname
///      HAERR_ISREALNAME, the input is local physical name, which cann't be used as virtual name
///      
/// @parameters:
///      szHostname  --- [in] the hostname need to be verified
///      pHACfg      --- [out] if the return value is HAOK_ISMASTER, the vHostname, vIPAddr will be set
///                            if the return value is HAOK_ISREPLICA, the replicaNode/IP will be set
///                            for other cases, don't change pHACfg
/// @comments: 
///		to verify master node, the expected return value must be HAOK_ISMASTER
///		to verify replica node, the expected return value must be HAOK_ISREPLICA
// =================================================================================================
DWORD Cst_verifyHostnameForSetup(WCHAR * szHostname, PBABHASETUPCFG pHACfg);

// =================================================================================================
/// -function: 
///		just apply for MSCS/NEC, check if the install path is located in shared disk
/// @return value: 
///		HAOK_NOERROR, the path is OK 
///		HAERR_NO_DISKRES, it is path in local disk 
///		HAERR_NO_NETRES, can't find virtual IP/hostname in the same cluster group
/// @parameters:
///		szHostname --- [in] the hostname need to be verified
///		pHACfg     --- [out] if the return value is HAOK_NOERROR, the vHostname, vIPAddr will be set
/// @comments:
///		the expected return value is HAOK_NOERROR
// =================================================================================================
DWORD Cst_verifyInstallPathForSetup(WCHAR * szPath, PBABHASETUPCFG pHACfg);

// =================================================================================================
/// -function: launch HA post-setup actions
/// @return value: 
///		HAOK_NOERROR, everyting is OK 
///		other: some error happend in post-setup
/// @parameters:
///		pHACfg --- [in] setup should fill everything and pass it into cluster API
/// @comments:
///		the expected return value is HAOK_NOERROR
// =================================================================================================
DWORD Cst_HAPostSetup(BOOL bIsSilentMode, PBABHASETUPCFG pHACfg);

// =================================================================================================
/// fucntion: get Database reconnection timeout from local HA configuartion file to deal with DB 
/// connection losing due to failover
/// @return value:
///      The timeout (in millisecond) value for DB reconnection, return 0 for non-cluster PS or MS
///      In case of DB connection failure, BAB services should retry to connect DB until timeout 
/// @parameters: 
///      none
/// @comments:
///      the API work as the "backend" of the api "ASgetDBReConnectTimeOut" of ascore.dll
///      the return value depends on the HA setting. If local machine is a cluster-aware BAB primary server
///      return a timeout set in setup phase, or else return 0; 
///      NOTE: other modules should NOT call this API straight. Instead, call ASgetDBReconnectTimeout
// ==================================================================================================
ULONG Cst_getDBReconnectTimeoutSetting();

/*end modification by wu$pu01 for HA_SETUP */

//================================================================================
//please disable this micro when you want to disable exception handler
#define EXCEPTION_HANDLER_ENABLE

#define	DUMPLEVEL_NORMAL	0
#define	DUMPLEVEL_HANDLE	1
#define	DUMPLEVEL_DATA		2
#define	DUMPLEVEL_MEMORY	3

void Cst_SetExceptionHandler(int nDumpLevel);

#ifdef EXCEPTION_HANDLER_ENABLE
#define CST_EXCEPTION_HANDLER(x)	Cst_SetExceptionHandler(x)
#else
#define CST_EXCEPTION_HANDLER(x)
#endif
//=================================================================================

#endif /* _CSTOOL_NOPROTOTYPE */

/* Server Migration Shared Memory related data structures and functions */

/* The increasing value denotes the order of Migration*/
#define		SERVERMIGRATION_PHASE_JOBSCRIPTS			0x01
#define		SERVERMIGRATION_PHASE_COREDB				0x02
#define		SERVERMIGRATION_PHASE_DRDATA				0x03
#define		SERVERMIGRATION_PHASE_LOGS					0x04
#define		SERVERMIGRATION_PHASE_AUTHDB				0x05
#define		SERVERMIGRATION_PHASE_SESSIONDETAILS		0x06
#define		SERVERMIGRATION_PHASE_CATALOG				0x07
#define		SERVERMIGRATION_PHASE_EQUIV					0x08
//////////////////////////////////////////////////////////////////////////////////////////////
typedef struct	tagASSERVERMIGRATIONSM
{
	unsigned long	ulMigrationStartTime;		//Time at which the migration started. Set by 1st active phase.

	unsigned long	ulCurrentMigrationPhase;	//Job/Auth/Log/DB/Catalog/DR/...
	unsigned long	ulTotalRecords;			//Total number of records to be migrated	. This can be 0.
	unsigned long	ulCurrentRecord;		//Current record # being migrated	
	unsigned long	ulMigrationPhaseStartTime;	//Time at which the migration started for current phase

	unsigned long	ulPreviousMigrationPhase;	//Job/Auth/Log/DB/Catalog/DR/...
	unsigned long	ulPreviousMigrationPhaseRecords;//Total number of records to migrated for previous phase.
	unsigned long	ulPreviousMigrationPhaseTimePeriod;	//Time at which the migration ended for Previous phase

	unsigned long	ulCanCancelCurrentMigrationPhase;	//Can we cancel the current migration ? Should be set for Catalogs
	unsigned long   ulCancelOperationRequest;	 //Set to 1 when the operation is being cancelled.
	unsigned long   ulCancelOperationRequestAck; //Set to 1 when the backend has received the request to cancel the operation.
	unsigned long   ulCancelOperationResponse;	 //Set to 1 when the operation has being cancelled successfully.

	unsigned long   ulNeedMigrationRecords;     //represent the records which really need to be migrated, because some records among total records will be skipped during migration
	unsigned long   ulSuccessfulMigrationRecords; //represent at the end how many records have been migrated successfully among records which really need to be migrated

	unsigned char	cReserved[128 - (14*sizeof(long))]; //modified from 12 to 14 after adding two ulong 
} ASSERVERMIGRATIONSM, *PASSERVERMIGRATIONSM;   /* Total size is 128 bytes */

/* Function prototypes */

// =================================================================================================
/// function: Initialize the SHM for server migration. Should be called only once per process.
/// @return value:0 is success
///				non-zero value is some error.
// =================================================================================================
int				CstInitServerMigrationSharedMemory();

// =================================================================================================
/// function: Free the SHM for ServerMigration. Should be called once per process.
/// @return value:0 is success
///				non-zero value is some error.
// =================================================================================================
void			CstFreeServerMigrationSharedMemory();

// =================================================================================================
/// function: Set current active migration phase 
/// @return value:0 is success
///				non-zero value is some error.
/// @parameters: 
///     ulCurrentPhase : Set to one of the SERVERMIGRATION_PHASE_xx values 
///		ulCanCancelMigrationPhase : Can the current phase be cancelled by user ?
// =================================================================================================
unsigned int	CstStartMigrationPhase(const unsigned long ulCurrentPhase, const unsigned long ulCanCancelMigrationPhase);

// =================================================================================================
/// function: Update the statistics for current migration phase.
/// @return value:0 is success
///				non-zero value is some error.
/// @parameters: 
///     ulTotalRecords : Set total number of records for migration 
///		ulCurrentRecord : Set current record # being migrated
// =================================================================================================
unsigned int	CstUpdateMigrationPhaseStatus(const unsigned long ulTotalRecords, const unsigned long ulCurrentRecord);

// =================================================================================================
/// function: Mark the current migration as done. This will update some internal statistics 
/// @return value:0 is success
///				non-zero value is some error.
// =================================================================================================
unsigned int	CstEndMigrationPhase();

// =================================================================================================
/// function: Can the current Migration be cancelled ? Boolean return
/// @return value:TRUE is success
///				0 value is some error.
// =================================================================================================
unsigned int	CstCanCancelCurrentMigration();

// =================================================================================================
/// function: Cancel the current migration phase
/// @return value:0 is success
///				non-zero value is some error.
// =================================================================================================
unsigned int	CstCancelCurrentMigration();

// =================================================================================================
/// function: Get the cancel operation's status.
/// @return value:0 is success
///				non-zero value is some error.
/// @parameters: 
///		pulCancelRequest(optional)	: Set to TRUE if currentmigration is requested to be cancelled.
///		pulCancelRequestAck(optional) ; Set to TRUE if migration module received this cancel request 
///		pulCancelRequestResponse(optional) : Set to TRUE if migration was cancelled.
// =================================================================================================
unsigned int CstGetCancelMigrationRequestStatus(unsigned long *pulCancelRequest,
												unsigned long *pulCancelRequestAck, 
												unsigned long *pulCancelRequestResponse);

// =================================================================================================
/// function: Update the reality statistics for current migration phase.
/// @return value:0 is success
///				non-zero value is some error.
/// @parameters: 
///     ulNeedMigrationRecords : represent the records which really need to be migrated, because some records among total records will be skipped during migration
///		ulSuccessfulMigrationRecords : represent at the end how many records have been migrated successfully among records which really need to be migrated
// =================================================================================================
unsigned int CstSetMigrationPhaseResult(const unsigned long ulNeedMigrationRecords, 
										const unsigned long ulSuccessfulMigrationRecords);

// =================================================================================================
/// function: Set the cancel operation's ack/response status.
/// @return value:0 is success
///				non-zero value is some error.
/// @parameters: 
///		ulCancelRequestAck ; Set to TRUE if migration module ack the request 
///		ulCancelRequestResponse : Set to TRUE if migration was cancelled.
// =================================================================================================
unsigned int CstUpdateCancelMigrationRequestStatus(const unsigned long ulCancelRequestAck, 
												const unsigned long ulCancelRequestResponse);

// =================================================================================================
/// function: Return the pointer to the SHM which holds the ServerMigration Status
/// @return value:0 is success
///				non-zero value is some error.
/// @parameters: 
///		ppServerMigrationInfo ; This will hold the SHM pointer
// =================================================================================================
unsigned int	CstGetMigrationPhaseDetails (PASSERVERMIGRATIONSM *ppServerMigrationInfo);

// =================================================================================================
/// function: Return current Phase's attributes
/// @return value:0 is success
///				non-zero value is some error.
/// @parameters: 
///		pulCurrentPhase (optional): Return current phase
///		pulTotalRecords (optional): Return current phase's total records
///		pulCurrentRecord (optional): Return current phase's current record
///		pulMigrationPhaseStartTime (optional): Return current phase's start time
// =================================================================================================
unsigned int	CstGetCurrentMigrationPhaseDetails(unsigned long *pulCurrentPhase, 
												   unsigned long *pulTotalRecords, 
												   unsigned long *pulCurrentRecord,
												   unsigned long *pulMigrationPhaseStartTime);

/////////////////////////////////////////////////////////////////////////////////////////////////
//Function: query if old ARCserve products installed on target computer
//
//return value:
//       -1  :   error - invalid parameters
//       -2  :   error - cannot login to computer
//       -3  :   error - cannot connect to remote registry
//        0  :   There is no ARCserve productes installed on target computer
//        1  :   There is r12 products installed on target computer
//        2  :   There is BAB 11.X products installed on target computer
//        3  :   There is BEB 10.X products installed on target computer
//
//Parameters:
//       serverName(MUST)  :  target computer name
//       userName(MUST)    :  user name
//       password(MUST)    :  can be NULL string, but cannot be NULL pointer
//
/////////////////////////////////////////////////////////////////////////////////////////////////
#if defined(UNICODE)
#define CstCheckInstalledOldProducts CstCheckInstalledOldProductsW
#else
#define CstCheckInstalledOldProducts CstCheckInstalledOldProductsA
#endif
int WINAPI CstCheckInstalledOldProducts(PTCHAR serverName, PTCHAR userName, PTCHAR password);

// Note:
// pSesshdrBuff points to the buffer contains both session header and the
// extended sesseion header. Because some agents do not write host
// name in session header, so we must parse the root directory in the 
// extended session header to retrieve the host name
int WINAPI CstGetHostFromSessionHeader(
								char * pSesshdrBuff, int nSesshdrBuffSize, 
								char * pszHostName, int nHostNameBufSize, 
								char * pszIPAddress, int nIPAddressBufSize);


#ifdef __cplusplus
  }
#endif /* __cplusplus */

#endif
