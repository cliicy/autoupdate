// SCONFIG.h : Defines the interface for the DLL.
//

#ifndef _SCONFIG_H
#define _SCONFIG_H

//#include <setupinfo.h>


#ifdef __cplusplus
extern "C"
{
#endif

//////////////////////////////////////////////////////////////////////////////////////
// ARCserve Remote Setup Function Prototypes
//////////////////////////////////////////////////////////////////////////////////////

// return code for CreateSilentConfigIni()
#define	E_SUCCESS		0		// INI file created successfully
#define E_FAILED		1		// Failed to Create INI file
#define	E_CANCEL		2		// User press "cancel" to close the dialog
#define	E_BACK			3		// Used only for MBO setup

typedef struct _RSETUP_PRODUCT_INFO
{
   TCHAR  szProductName[100];				// pointer to the product name (e.g. ARCserve for Windows NT - Enterpise Edition)
   BOOL	 bARCserveRequired;					// whether it requries ARCserve to be installed first
   BOOL  bSettingRequired;					// enable/disable Settings button in Remote setup
   //BOOL  bValidateDefaults;				    // enable/disable the validation of the defaults in the configuration file, when Settings required
   BOOL  bAlreadyInstalled;					// TRUE, if product already installed to remote computer
   TCHAR  szInstallPath[MAX_PATH+1];			// default path or the product path, if product already installed
} RSETUP_PRODUCT_INFO;

__declspec(dllexport) BOOL GetProductInfo(			// get remote setup product information
	IN OUT RSETUP_PRODUCT_INFO* pRSetupProductInfo
	);							// return TRUE when successful

__declspec(dllexport) BOOL AllowInstallation(			// whether the product could be installed on the remote computer.
	IN		LPTSTR lpMachine,	// target (remote) computer on which product to be installed
	IN		LPTSTR lpUserName,	// user name
	IN		LPTSTR lpPassword,	// password
	IN OUT	LPTSTR lpMessage,	// pointer to the error message buffer
	IN		INT	  nMaxLength,	// maxim size of the error message buffer
	IN OUT	RSETUP_PRODUCT_INFO* pRSetupProductInfo 
								// function should set the default path (or the path of the product if already installed), and
								// the bAlreadyInstalled flag 
	);							// returns TRUE if installation allowed

__declspec(dllexport) BOOL GetSilentConfigDefaults( // get remote defaults settings and 
									                // create the INI file 
	IN	    LPTSTR szINIPath,	// directory path of the INI file (e.g. c:\temp\server_1)
	IN		LPTSTR lpMachine,	// target (remote) computer on which product to be installed
	IN		LPTSTR lpUserName,	// user name
	IN		LPTSTR lpPassword	// password
	);

__declspec(dllexport) INT CreateSilentConfigIni( // creates dialog and asks for setup options;
												 // save user configuration to the "arcsetup.icf" if user 
												 // close the dialog via "OK" button
	IN		LPTSTR lpMachine,	// target (remote) computer on which product to be installed
	IN		LPTSTR lpUserName,	// user name
	IN		LPTSTR lpPassword,	// password
	IN		HWND hParent,		// parent of the dialog
	IN OUT	LPTSTR szINIPath,	// directory path of the INI file (e.g. c:\temp\server_1), once return the value changed to "c:\temp\server_1\arcsetup.inf")
	IN OUT	RSETUP_PRODUCT_INFO* pRSetupProductInfo // function should set installation path 
	);

__declspec(dllexport) BOOL IsConfigIniValid( // validate settings in configuration file
	IN	   LPTSTR szINIPath,		// directory path of the INI file (e.g. c:\temp\server_1)
	IN OUT LPTSTR lpMessage,		// pointer to the error message buffer
	IN     INT	 nMaxLength     // maxim size of the error message buffer
	);

// typedef functions for dynamic loading
typedef BOOL    (*PFNGETPRODUCTINFO)( RSETUP_PRODUCT_INFO* );
typedef BOOL    (*PFNALLOWINSTALLATION)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, INT, RSETUP_PRODUCT_INFO* );
typedef INT     (*PFNCREATESCONFIGINI)( LPTSTR, LPTSTR, LPTSTR, HWND, LPTSTR, RSETUP_PRODUCT_INFO* );
typedef BOOL    (*PFNISCONFIGINIVALID) ( LPTSTR, LPTSTR, INT );
typedef BOOL    (*PFNGETDEFAULTS) ( LPTSTR, LPTSTR, LPTSTR, LPTSTR );


//////////////////////////////////////////////////////////////////////////////////////
// RSetup Moinotr, mailslot command format "Servename\\setup_phase\\status"
//////////////////////////////////////////////////////////////////////////////////////

// constants used to report back the phase of the remote installation
#define RSMP_INITIALIZE					1	// Initializing
#define RSMP_COPYING_FILES				2	// Copying Files
#define RSMP_INSTALLING_SRV				3	// Installing Service
#define RSMP_INSTCOMPLETED				4	// Installation Completed
#define RSMP_INPROGRESS					5   // Installation in Progress
#define RSMP_ERROR_COPYFILES			6   // Error Copying Files
#define RSMP_ERROR_NO_SPACE				7   // Not Enough Space
#define RSMP_ALREADY_RUNNING			8	// Setup Already Running
#define RSMP_ERROR_STARTSRV				9	// Failed to Start Remote Service
#define RSMP_REMOVING_SRV				10	// Removing Existing Services
#define RSMP_CREATING_REGISTRY			11  // Creating Registries
#define RSMP_CREATING_ICANDFLD			12  // Creating Icons and Folders
#define RSMP_LOAD_SETUP_FAILED			13  // Unable to Load Setup DLL files
#define RSMP_ARCSERVE_NOT_FOUND			14  // ARCserve for Windows NT Was Not Found
#define RSMP_REQUIRE_CHANGER			15  // ARCserve Changer Option was not found
#define RSMP_ERROR_REGISTRY				16  // Failed to Create Registry Entries
#define RSMP_ERROR_CREATESRV			17  // Failed to Create Services
#define RSMP_SNABASE_NOT_FOUND			18  // SNABase service not found
#define RSMP_INIFILE_NOT_FOUND			19  // Failed to read configuration file
#define RSMP_MVS_SERVICE_RUNNING		20  // MVS service already running
#define RSMP_IMPROPER_EDITION			21  // Failed to install improper edition of CA product
#define RSMP_OPTION_NOTAVAILABLE        22  // Failed to install invalid edition of CA product
#define RSMP_RELATED_PROCESS_RUNNING	23	// The related process is running
#define RSMP_MVS_JOB_RUNNING		    24  // A MVS job is running
#define RSMS_WINVERSION_ERR		        25  // Can not find the Windows 2000 machine.
#define RSMP_INSTNOTCOMPLETED			26	// Installation Uncompleted

#define RSMP_INSTWINDOWSMSI				27  // Installing Windows Installer
#define RSMP_UPDATEIE5					28	// Installing IE 5.0
#define RSMP_UPDATESYSLIB				29	// Updating Windows System Librarys
#define RSMP_REBOOT						30	// Need reboot after installation
#define RSMP_REBOOT_REINSTALL			31	// Reboot first then reinstall product

#define MAX_NO_STATUS					31

// constants used to report back the status of the remote installation
#define RSMS_IN_PROGRESS			1  
#define RSMS_SUCCESS				2
#define RSMS_FAILED					3
#define RSMS_NOT_STARTED			4

#define RSMS_MAX					4

#ifdef __cplusplus
}
#endif

#endif //_SCONFIG_H

