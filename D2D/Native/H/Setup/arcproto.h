/* ---------------------------------------------------------------------- */
/*               Proprietary and Confidential Information                 */
/*                                                                        */
/*   Copyright (c) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.                                      */
/*                                                                        */
/* ---------------------------------------------------------------------- */
/*   MS-VSS Module Control Stamp                                          */
/* ---------------------------------------------------------------------- */

#ifndef _VSS_Id_ARCPROTO_H_
#define _VSS_Id_ARCPROTO_H_

static char _VSS_Id_ARCPROTO_H[]  = "$Header: /ASNT.61/ARCSETUP.DLL/arcproto.h 7     10/26/98 4:51p Ivan $";

/*$Log: /ASNT.61/ARCSETUP.DLL/arcproto.h $
 * 
 * 7     10/26/98 4:51p Ivan
 * Add ARCShutdown()
 * 
 * 4     9/25/98 3:09p Ivan
 * change ARCShutdown() return type to BOOL
 * 
 * 3     9/24/98 5:39p Ivan
 * Add ARCShutdown() 
 * 
 * 2     3/03/98 12:59p Ivan
 * 
 * 1     3/03/98 12:43p Ivan
 * 
 * 5     2/09/98 7:36p Ivan
 * 
 * 2     2/04/98 6:16p Warmi01
 * Add ARCUnInstall()
 * 
 * 1     1/28/98 4:13p Warmi01
 * ARCserver setup DLL API function prototypes
 */

//////////////////////////////////////////////////////////////////////////////////////
// ARCserve Setup Function Prototypes
//////////////////////////////////////////////////////////////////////////////////////

BOOL    ARCInstallNotify(                       // Notification of component install
                    IN  LPTSTR  pCaller         // Calling component (e.g. "Enterprise Management")
        );                                      // (return TRUE when successful)

BOOL    ARCUnInstallNotify(                     // Notification of component uninstall
                    IN  LPTSTR  pCaller         // Calling component (e.g. "Enterprise Management")
        );                                      // (return TRUE when successful)

BOOL    ARCGetSpaceNeeded(                      // Get space required to install ARCserve 
                    OUT double  *pSpace         // Space required (in bytes)
        );                                      // (return TRUE when successful)

BOOL    ARCGetInstallType(                      // Get installation status of ARCserve
                    OUT UINT    *pType,         // Installed ARCserve type
                    OUT double  *pVersion       // Installed ARCserve version (e.g. "2.1")
        );                                      // (return TRUE when successful)

//      pType settings...
enum _ARCtype {
    ARCINS_NONE,                                //  Not installed
    ARCINS_SINGLSERVR,                          //  Single Server installed
    ARCINS_ENTERPRISE,                          //  Enterprise edition installed
    ARCINS_ASO                                  //  ASO installed
};

BOOL    ARCGetARCservePath(                     // Get path of installed (not default) ARCserve component
                    OUT LPTSTR  pPath           // Path buffer
        );                                      // (return TRUE when successful)

BOOL    ARCGetAlertPath(                        // Get path of installed (not default )Alert component
                    OUT LPTSTR  pPath           // Path buffer
        );                                      // (return TRUE when successful)

BOOL    ARCGetIconDefsFile(                     // Get path of icon definitions file
                    OUT LPTSTR  pPath           // Path buffer
        );                                      // (return TRUE when successful)

BOOL    ARCCheckSetupValues(                    // Check .INI setup values
                    IN  DWORDLONG CheckOpts,    // Flag items to be checked
                    IN  LPTSTR  pIniFile        // .INI file path
        );                                      // (return TRUE when successful)
//      CheckOpts settings...
#define ARCCHK_ALL          0xFFFFFFFFFFFFFFFF  // Checks all applicable values
#define ARCCHK_ARCPATH      0x0000000000000001  //   "  ARCserve installation path
#define ARCCHK_ALERTPATH    0x0000000000000002  //   "  Alert installation path
#define ARCCHK_FULLNAME		0x0000000000000004
#define ARCCHK_COMPANYNAME  0x0000000000000008
#define ARCCHK_CDKEY		0x0000000000000010
#define ARCCHK_DOMAINNAME   0x0000000000000020
#define ARCCHK_USERNAME		0x0000000000000040
#define ARCCHK_PASSWORD		0x0000000000000080
#define ARCCHK_INSTALL		0x0000000000000100
#define ARCCHK_DBOPTION		0x0000000000000200
#define ARCCHK_SQLINFO		0x0000000000000400
#define ARCCHK_UNINSTALL	0x0000000000000800

BOOL    ARCserveSetup(                          // Launch ARCserve setup program
                    IN  LPTSTR  pCaller,        // Calling component (e.g. "Enterprise Management")
                    IN  LPTSTR  pIniFile,       // .INI file
                    IN  LPTSTR  pInstallImage,  // CD install image path
                    IN  LPTSTR  pCDkey,         // CD key value
                    IN  LPTSTR  pPassword,      // Account password
                    IN  ...                     // (reserved for last minute)
        );                                      // (return TRUE when successful)

BOOL    ARCserveSetupV(                         // Launch ARCserve setup program
                    IN  LPTSTR  pCaller,        // Calling component (e.g. "Enterprise Management")
                    IN  LPTSTR  pIniFile,       // .INI file
                    IN  LPTSTR  pInstallImage,  // CD install image path
                    IN  LPTSTR  pCDkey,         // CD key value
                    IN  LPTSTR  pPassword,      // Account password
                    IN  va_list argList         // (reserved for last minute)
        );                                      // (return TRUE when successful)

BOOL    ARCclientSetup(                         // Launch ARCserve client setup program
                    IN  LPTSTR  pCaller,        // Calling component (e.g. "Enterprise Management")
                    IN  LPTSTR  pIniFile,       // .INI file
                    IN  LPTSTR  pInstallImage,  // CD install image path
                    IN  LPTSTR  pCDkey,         // CD key value
                    IN  ...                     // (reserved for last minute)
        );                                      // (return TRUE when successful)

BOOL    ARCclientSetupV(                        // Launch ARCserve client setup program
                    IN  LPTSTR  pCaller,        // Calling component (e.g. "Enterprise Management")
                    IN  LPTSTR  pIniFile,       // .INI file
                    IN  LPTSTR  pInstallImage,  // CD install image path
                    IN  LPTSTR  pCDkey,         // CD key value
                    IN  va_list argList         // (reserved for last minute)
        );                                      // (return TRUE when successful)

BOOL    ARCUnInstall(                           // Launch ARCserve uninstall program
                    IN  LPTSTR  pCaller         // Calling component (e.g. "Enterprise Management")
        );                                      // (return TRUE when successful)

LPTSTR  ARCGetMsgString();                      // Get last message string (produced by prior error)

BOOL	ARCShutdown();							// Stop all ARCserve service, Mangaer and Alert service

// typedef functions for dynamic loading
typedef BOOL    (*ARCINSTALLNOTIFY)( LPTSTR );
typedef BOOL    (*ARCUNINSTALLNOTIFY)( LPTSTR );
typedef BOOL    (*ARCGETSPACENEEDED)( double * );
typedef BOOL    (*ARCGETINSTALLTYPE)( UINT *, double * );
typedef BOOL    (*ARCGETARCSERVEPATH)( LPTSTR );
typedef BOOL    (*ARCGETALERTPATH)( LPTSTR );
typedef BOOL    (*ARCGETICONDEFSFILE)( LPTSTR );
typedef BOOL    (*ARCCHECKSETUPVALUES)( DWORDLONG, LPTSTR );
typedef BOOL    (*ARCSERVESETUP)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, LPTSTR, ... );
typedef BOOL    (*ARCSERVESETUPV)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, LPTSTR, va_list );
typedef BOOL    (*ARCCLIENTSETUP)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, ... );
typedef BOOL    (*ARCCLIENTSETUPV)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, va_list );
typedef BOOL    (*ARCUNINSTALL)( LPTSTR );
typedef LPTSTR  (*ARCGETMSGSTRING)();
typedef BOOL	(*ARCSHUTDOWN)();

BOOL WINAPI PatchASFiles (LPCTSTR lpSourceDir);
BOOL IsAppRunning(LPCTSTR lpszMutex);
BOOL	CloseApp (LPCTSTR lpszClassName);
BOOL	StopService (LPCTSTR lpszServiceName);
BOOL WINAPI CopyASFile (LPCTSTR lpSource, LPCTSTR lpTarget);

#endif

