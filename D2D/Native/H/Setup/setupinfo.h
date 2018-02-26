// SetupInfo.h : Defines the parameters for setup.
//

#ifndef _SETUPINFO
#define _SETUPINFO

typedef struct tagSETUP_INFO{
    TCHAR szProductName[256];           // name of product being installed (used for dialogs, strings, etc.)
    TCHAR szProfile[MAX_PATH+1];        // profile .ini
    TCHAR szTraceFile[MAX_PATH+1];      // log file to trace install (if bTrace)
    TCHAR szSourcePath[MAX_PATH+1];     // directory of source
    TCHAR szBatchFile[MAX_PATH+1];      // batch file (if bSilentInstall)
    TCHAR szMIFFile[MAX_PATH+1];        // MIF file (if bMIF)
    TCHAR szProgramGroup[256];          // program group name
    TCHAR szLogFile[MAX_PATH+1];        // error log file for silent install.
	DWORD dwProductType;
    BOOL  bIgnoreErrors;                // ignore errors?
    BOOL  bTrace;                       // log steps to trace file
    BOOL  bSilentInstall;               // batch operation?
    BOOL  bMIF;                         // write to mif
    BOOL  bConfigurationOnly;           // used by option setups
	BYTE  dProdSubType;					// product subtype 
    BOOL  bDeleteUninstall;				// used by uninstall
    TCHAR szUninstallEXE[MAX_PATH+1];   // the uninstall module name
    BOOL  bCopyOnly;					// used by option setups (only update the modules)
} SETUP_INFO, *LPSETUP_INFO;

#endif
