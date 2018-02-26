// template.h : Defines the interface for the Master Setup.
//
#ifndef _MASTERSETUP_H__
#define _MASTERSETUP_H__

#ifdef __cplusplus
extern "C"
{
#endif

//caowe01: 2007-03-12 for new gui
//#define _MAX_DEPENDENT_					16
#define _MAX_DEPENDENT_					64
//caowe01: end
#define MSETUP_TYPE_LOCAL				0
#define MSETUP_TYPE_REMOTE				1
#define MSETUP_TYPE_RESPONSE			2
#define MSETUP_TYPE_EXPRESS				3
#define MSETUP_TYPE_SILENCE				4

typedef struct _PROD_CONFIG_INFO
{
	TCHAR	szCfgPageTitle[MAX_PATH];
	TCHAR	szInfo[MAX_PATH];
} PROD_CONFIG_INFO;

typedef struct _PROD_3RD_INFO
{
	TCHAR	sz3rdPackage[MAX_PATH];
	TCHAR	sz3rdPackFolder[MAX_PATH];
	TCHAR	sz3rdMessage[MAX_PATH];
	BOOL	bIsCriticalReboot; 
} PROD_3RD_INFO;

typedef struct _MSETUP_PRODUCT_INFO
{
   TCHAR szProductName[100];				// pointer to the product name (e.g. ARCserve for Windows NT - Enterpise Edition)
   BOOL	 bARCserveRequired;					// whether it requries ARCserve to be installed first
   BOOL  bSettingRequired;					// enable/disable to run configuration tools in master setup
   BOOL  bAlreadyInstalled;					// TRUE, if product already installed to remote computer
   TCHAR szInstallPath[MAX_PATH+1];			// default path or the product path, if product already installed
   BOOL	 bIsGoNext;							// Chong 6/6/02 --- it is used in control product configuration GUI sequence
											// [IN]		bIsGoNext = TRUE, It means product configuration GUI should start from the first page.
											//			bIsGoNext = FALSE, It means product configuration GUI should start from the last page.
											// [OUT]	bIsGoNext = TRUE, It means user existed product configuration GUI from click Next Button	
											//			bIsGoNext = FALSE, It means user existed product configuration GUI from click Back Button	
   BOOL	bIsNeedPostCfg;						// TRUE, if need configuration after installation. Otherwise it is FALSE.
   UINT	iMasterSetupType;					// flag declare which mode master setup running
   TCHAR szFeatureList[_MAX_DEPENDENT_][MAX_PATH];
   PROD_3RD_INFO	prod3rdInfo[_MAX_DEPENDENT_];
   PROD_CONFIG_INFO prodConfigInfo[_MAX_DEPENDENT_];
} MSETUP_PRODUCT_INFO, *LPMSETUP_PRODUCT_INFO;




#define _DRIVE_NUM						26
#define ACCOUNT_NAME_LIMIT				48
#define PASSWORD_LIMIT					48

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
#define RSMP_CONFIG_PRODUCT				32	// Now Configurating the product
#define RSMP_UPDATE_PRODUCT				33	// Now Updating the product
#define RSMP_SEND_LOG_FILENAME			34	// Now Sending the log file full name
#define RSMP_ERROR_INSTSP				35	// Failed to install service package!
#define RSMP_STOP_SERVICE				36	// Stopping services
#define RSMP_START_SERVICE				37	// Starting services
#define RSMP_INSTALLING_EXTERNAL		38	// Install external components

#define MAX_NO_PHASE					38

// constants used to report back the status of the remote installation
#define RSMS_IN_PROGRESS			1  
#define RSMS_SUCCESS				2
#define RSMS_FAILED					3
#define RSMS_NOT_STARTED			4
#define RSMS_INST_PRE				5
#define RSMS_COPY_IMG				6
#define RSMS_WAITING				7

#define MAX_NO_STATUS				8

//////////////////////////////////////////////////////////////////////////////////////
// constants used to return back the status of the check install info
//////////////////////////////////////////////////////////////////////////////////////
#define MSETUP_NOT_ALLOW_INSTALL	(0x00000000)
#define MSETUP_ALLOW_INSTALL		(0x00000001)
#define MSETUP_DEFAULT_SELECT		(0x00000002)
#define MSETUP_ALREADY_INSTALLED	(0x00000004)
#define MSETUP_NEED_UPGRADE			(0x00000008)
#define MSETUP_FORBID_INSTALL		(0x00000010)

//product selection with all features added by fanzh01 on 9/25/2008
#define MSETUP_DEFAULT_SELECT_ALL	(0x00000020) 

//////////////////////////////////////////////////////////////////////////////////////
// ARCserve Setup Function Prototypes
//////////////////////////////////////////////////////////////////////////////////////
#ifdef _MASTER_SETUP
typedef DWORD   (*PFNCHECKINSTALLINFO)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, INT, MSETUP_PRODUCT_INFO*, LPTSTR, LPTSTR, BOOL);
typedef BOOL    (*PFNCREATECONFIGPROFILE)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, INT, MSETUP_PRODUCT_INFO*, LPTSTR, LPTSTR);
typedef BOOL    (*PFNGETPOSTCONFIGINFO)( LPTSTR, LPTSTR, LPTSTR, LPTSTR, INT, MSETUP_PRODUCT_INFO*,LPTSTR);

#else
__declspec(dllexport) DWORD CheckInstallInfo(			// whether the product could be installed on the remote computer.
	IN		LPTSTR lpMachine,	// target computer on which product to be installed
	IN		LPTSTR lpUserName,	// user name
	IN		LPTSTR lpPassword,	// password
	IN OUT	LPTSTR lpMessage,	// pointer to the error message buffer
	IN		INT	  nMaxLength,	// maxim size of the error message buffer
	IN OUT	MSETUP_PRODUCT_INFO* pMSetupProductInfo, // structure that include dependent imformation
	IN		LPTSTR lpICFFileName,     // full name of SETUP.ICF file
	IN		LPTSTR lpINFFilePath,     // full path of SETUP.INF file
	IN		BOOL  bCheckAllowInstall // Check environment flag
	);							// returns TRUE if installation allowed

__declspec(dllexport) BOOL CreateConfigProfile(			// set logon info, install path and features status, call configuration tool
	IN		LPTSTR lpMachine,	// target computer on which product to be installed
	IN		LPTSTR lpUserName,	// user name
	IN		LPTSTR lpPassword,	// password
	IN OUT	LPTSTR lpMessage,	// pointer to the error message buffer
	IN		INT	  nMaxLength,	// maxim size of the error message buffer
	IN		MSETUP_PRODUCT_INFO* pMSetupProductInfo, // structure that include dependent imformation
	IN		LPTSTR lpICFFileName,             // full name of SETUP.ICF file
	IN		LPTSTR lpINFFilePath // full path of SETUP.INF file
	);							// returns TRUE if call configuration tool success

__declspec(dllexport) BOOL GetPostConfigInfo(			// launch configuration tools to finish installation
	IN		LPTSTR lpMachine,	// target computer on which product to be installed
	IN		LPTSTR lpUserName,	// user name
	IN		LPTSTR lpPassword,	// password
	IN OUT	LPTSTR lpMessage,	// pointer to the error message buffer
	IN		INT	  nMaxLength,	// maxim size of the error message buffer
	IN		MSETUP_PRODUCT_INFO* pMSetupProductInfo,  // structure that include dependent imformation, Now only use to get install path
	IN		LPTSTR lpICFFileName  // full name of SETUP.ICF file
	);

#endif


#ifdef __cplusplus
}
#endif

#endif
