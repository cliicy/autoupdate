#if !defined(AGENTDEPLOYT_H)
#define AGENTDEPLOY_H

#if   _MSC_VER   >   1000   
#pragma   once   
#endif   //   _MSC_VER   >   1000   

#include <string>

using namespace std;

#define ADT_ERR_MULTI_INSTANCE		80000	//A Setup for ^ADR^ is running on this system.
#define ADT_ERR_CHECKSUM			80001	//The Agent Deployment package has been damaged, need to reinstall it!
#define ADT_ERR_GETTEMPPATH			80002	//Cannot get Temp directory!
#define ADT_ERR_TIMEOUT				80003	//Cannot get installation status on target host.	
#define ADT_ERR_LOGONUSER			80004	//Log on user failed.
#define ADT_ERR_IMPERSONATE			80005	//Impersonate privilege failed.
#define ADT_ERR_CREATEPROCESSASUSER	80006	//Launch deployment failed.
#define ADT_ERR_CREATEFOLDER		80007	//Folder cannot be created.
#define ADT_ERR_ADTPACKAGENOTFOUND	80008   //Cannot get Agent Deployment Package Path
#define ADT_ERR_RESDLLNOTFOUND		80009	//The resource dll is not found in current directory.	
#define ADT_ERR_RESININOTFOUND		80010	//The resource ini file is not found.
#define ADT_ERR_UNKNOWNOS			80011	//Setup cannot recognize the current operating system!	
#define ADT_ERR_GETHOSTNAME			80012	//Setup fails to get computer name.
#define ADT_ERR_PRODUCTLIST			80013	//Setup fails to generate product list.	
#define ADT_ERR_CMDLINE				80014	//Command line is invalid.
#define ADT_ERR_ICFNOTFOUND			80015	//Cannot found installation configuration file.
#define ADT_ERR_ADMINRIGHTS			80016	//You must have Windows administrative privileges to deploy agents to remote hosts.
#define ADT_ERR_ISALLOWINSTALL		80017	//The target host does not allow install products.
#define ADT_ERR_BLOCKOS				80018	//Setup package cannot support the operating system on the target host.
#define ADT_ERR_CREATEEVENT			80019	//Internal error. CreateEvent Failed.
#define ADT_ERR_COPYRMTSERVICE		80020	//Cannot copy remote installation service to target host.
#define ADT_ERR_STARTRMTSERVICE		80021	//Cannot start remote installation service in target host.	
#define ADT_ERR_FINDFIRSTDRIVE		80022	//Setup cannot find first hard drive.
#define ADT_ERR_COPYFILE2RMT		80023	//Copy files to target host failed.
#define ADT_ERR_INSTPRE				80024	//Install prerequisite software failed.
#define ADT_ERR_COMPONENT			80025	//The installation of required component is failed. Setup can not continue.		
#define ADT_ERR_INVALIDHOSTNAME		80026	//Invalid host name
#define ADT_ERR_INVALIDUSERNAME		80027	//Invalid user name.
#define ADT_ERR_IPNOTSUPPORTED		80028	//IP address is not supported, please enter computer name instead.
#define ADT_ERR_LOCALCOMPUTER		80029	//Please do not enter your host computer as one of the remote setup computers.
#define ADT_ERR_BLOCKCLUSTER		80030	//Agent Deployment cannot continue.Agent Deployment does not support deploying agents to remote cluster-aware environments.
#define ADT_ERR_HIGHERVER			80031	//Setup cannot continue because the specified agents on the target host have a newer version than this release.
#define ADT_ERR_ALREADYINSTALLED	80032	//^ADR^ have been installed on some hosts. These hosts will be removed from the deployment.	
#define ADT_ERR_BAD_NETPATH			80033	//Setup cannot connect to %s.\n\nThe network share \\\\%s\\admin$ is not available via the network.\n\nTo remedy this problem, you must enable the \\\\%s\\admin$ share on the target host.
#define ADT_ERR_ACCESS_DENIED		80034   //Setup cannot connect to %s.\n\nThe account specified does not have privileges to connect to \\\\%s\\admin$.\n\nTo remedy this problem, you must use an account that has administrative privileges to connect to \\\\%s\\admin$.
#define ADT_ERR_LOGON_FAILURE		80035	//Logon failure: unknown user name or bad password. Please use an account that has administrative privileges to connect to \\\\%s\\admin$
#define ADT_ERR_CONNECT_FAILURE		80036   //Could not connect to %s. Please consult your network administrator.
#define ADT_ERR_CONNECT_LASTERR		80037	//Setup cannot connect to %s.\n\nLast error code from Windows is %d. Please consult to your network administrator.\n\nTo remedy this problem, you must use an account that has administrative privileges to connect to \\\\%s\\admin$.
#define ADT_ERR_RMTREGSERVICE		80038	//Failed to retrieve system information from the target host %s.\n\nSetup is unable to start the Windows Remote Registry service on the target host.
#define ADT_ERR_CHECKADMINRIGHTS	80039	//Setup cannot retrieve system information from the target host registry.\n\nLast error code from Windows is %d. Please consult to your network administrator.
#define ADT_ERR_NOSHAREDRIVE		80040	//Setup cannot detect a default drive share on %s.\n\nSetup requires a drive share that is accessible on the target host.\n\nTo remedy this problem, you must set up a default drive share on the target host.
#define ADT_ERR_SESSCREDCONFLICT	80041	//Multiple connections to a server or shared resource by the same user, using more than one user name, are not allowed. Disconnect all previous connections to the server or shared resource and try again.
#define ADT_ERR_WOW64				80042	//WOW64 is not enalbed on machine %s. Agent deployment will be terminate!
#define ADT_ERR_DEPLOYPOLICY_BLOCK	80043	//The target host does not allow install products due to deploy policy confliction
#define ADT_ERR_INST_AFTER_REBOOT	80044	//Setup has upgraded critical system files. To continue with Setup, you must restart the remote system now and then restart Setup.
#define ADT_ERR_INST_PRE_REBOOT		80045	//Setup has completed installing OS pre-requisites for this product which now requires a system reboot. You need to launch Setup again to complete the installation.
#define ADT_ERR_LAUNCHPROCESS		80046	//Internal error, launch process failed
#define ADT_ERR_GETLANG				80047	//Cannot get user selected language
#define ADT_ERR_CREATEWND			80048	//Internal error, create window failed
#define ADT_ERR_WIN2KBELOW			80049	//^ADR^ cannot be installed on Windows 2000 machines
#define ADT_ERR_IA					80050	//^ADR^ cannot be installed on Itanium System machines.
#define ADT_ERR_JOB_RUNNING			80051	//Setup detected that at least one active job is running on the machine. Setup can not continue. Please stop all the running job and try the installation again.
#define ADT_ERR_INVALID_INSTPATH	80052	//The specified install path is invalid on target host
#define ADT_ERR_DISKSIZE			80053	//The disk size is not enough on the target host
#define ADT_ERR_CHECKENV			80054	//Collect environment information failed.
#define ADT_ERR_UNINST_XPKI_REBOOT	80055	//The target host needs to reboot first because of ETPKI(CAPKI) was uninstalled without reboot.
#define ADT_ERR_WIN2k3SP1BELOW		80056	//^ADR^ can not be installed on Windows 2003 with servie pack lower than SP1
#define ADT_ERR_ENCRYPT				80057	//Internal error, encrypt information failed.
#define ADT_ERR_DECRYPT				80058	//Internal error, decrypt information failed.
#define ADT_ERR_LOCALADTPATH		80059	//Cannot get D2D install path on local host.
#define ADT_ERR_B4INSTPRE_REBOOT	80060	//Setup cannot install ^ADR^ because a reboot is required to complete the previous uninstallation. Please restart your system first and then attempt the setup again.
#define ADT_ERR_UPG_KEEPCONFIG		80061	//Need to comply with the original configuration for the upgrade.
#define ADT_ERR_INSTALL_FAIL		80062	//Installation failed, please check log file for detail information.
#define ADT_ERR_UA_RUNNING			80063	//Setup detected that CA ARCserve Backup Universal Agent service is running on the target machine. To update CA ARCserve D2D program, please stop this service and try the installation again.
#define ADT_ERR_HBA_STOPPED			80064	//Setup is unable to stop the CA ARCserve D2D Mount Driver service. Please run the following command line to remove the CA ARCserve D2D Mount Driver service: <D2DHOME>\BIN\DRIVER\UninstallHBADriver.bat. Upon completion of this operation, you will need to reboot the machine and rerun setup.
#define ADT_ERR_WEBSERVER_REBOOT	80065   //Setup has removed the CA ARCserve D2D Web Service and requires rebooting the system. To continue with Setup, you must restart the system now and then rerun Setup. 
#define ADT_ERR_INSTALL_SERVICE			80066   //Failed to install CA ARCserve D2D Service.
#define ADT_ERR_INSTALL_VOLUMEDRIVER	80067   //Failed to install CA ARCserve D2D Volume Driver.
#define ADT_ERR_INSTALL_MOUNTDRIVER		80068   //Failed to install CA ARCserve D2D Mount Driver. 
#define ADT_ERR_INSTALL_INTERFACEDRIVER	80069   //Failed to install CA ARCserve D2D Interface Driver. 
#define ADT_ERR_PORT_OCCUPIED			80070   //The port number is invalid. It is being used by another program. Please input a different value. 
#define ADT_ERR_WINXPX86				80075	//CA ARCserve D2D can not be installed on Windows XP (X86) with servie pack lower than SP3
#define ADT_ERR_WINXPX64				80076	//CA ARCserve D2D can not be installed on Windows XP (X64) with servie pack lower than SP1
#define ADT_ERR_NOTSTARTRMTREG		80080	//Setup failed to retrieve system information from the target host %s.\n\nThe Windows Remote Registry service is not running or disabled on the target host. Please ensure the Windows Remote Registry service is in running state.
#define ADT_SUCCESS_REBOOT			80100	//Please restart the machine, otherwise the installed product(s) will not work properly.
#define ADT_MSG_NOT_STARTED			80200	//waiting...
#define ADT_MSG_WAITING				80201	//waiting...
#define ADT_MSG_COPY_IMG			80202	//copying image...
#define ADT_MSG_IN_PROGRESS			80203	//installing...
#define ADT_MSG_INST_PRE			80204	//installing prerequisites...
#define ADT_SUCCESS					80205	//Deploy succeed.
#define ADT_STATUS_ALIVE			80300	//For remote heart beat		


typedef struct _D2DREGCONF
{
	wchar_t wszHostName[MAX_PATH];
	wchar_t wszInstedPath[MAX_PATH];
	wstring * pwsUUID;
	DWORD	dwPort;
	DWORD	dwBuild;
	DWORD	dwMajVer;
	DWORD	dwMinVer;
	_D2DREGCONF * pNext;
}D2DREGCONF, *PD2DREGCONF;

typedef struct _D2DREGCONF2
{
	wchar_t wszHostName[MAX_PATH];
	wchar_t wszInstedPath[MAX_PATH];
	wstring * pwsUUID;
	DWORD	dwPort;
	DWORD	dwBuild;
	DWORD	dwMajVer;
	DWORD	dwMinVer;
	BOOL    bDriverInstalled;
	int		nCommType;
	int		nMonitorOption;
	_D2DREGCONF * pNext;
}D2DREGCONF2, *PD2DREGCONF2;

typedef int (__stdcall *DeployStatusFunction)(const wchar_t*, // Server Name
										 const UINT,	 // Status: 1-7:
										 const wchar_t*, // Status message
										 const UINT,	 // Percentage: 0-100
										 const DWORD
										 );

typedef UINT (__cdecl *pfnStartToDeploy)(wchar_t* szwAFDomain, // AF UI logon 
										 wchar_t* szwAFUserName, // AF UI logon
										 wchar_t* szwAFPassword, // AF UI logon
										 wchar_t* szwUUID,	  // UUID from JAVA side
										 wchar_t* szwServerName, // Remote server name
										 wchar_t* szwUserName,   // Log on User with administrator privilege
										 wchar_t* szwPassword,   // Password
										 UINT	 iPort,       // Port number
										 wchar_t* szwInstallDir,
										 BOOL   bAutoStartRRService,
										 BOOL	bIsReboot,
										 BOOL	bResumedAndCheck,
										 DeployStatusFunction deployStatusFunc); // callback function

typedef UINT (__cdecl *pfnStartToDeploy2)(wchar_t* szwAFDomain, // AF UI logon 
										 wchar_t* szwAFUserName, // AF UI logon
										 wchar_t* szwAFPassword, // AF UI logon
										 wchar_t* szwUUID,	  // UUID from JAVA side
										 wchar_t* szwServerName, // Remote server name
										 wchar_t* szwUserName,   // Log on User with administrator privilege
										 wchar_t* szwPassword,   // Password
										 UINT	 iPort,       // Port number
										 wchar_t* szwInstallDir,
										 BOOL   bAutoStartRRService,
										 BOOL	bIsReboot,
										 BOOL   bInstallDriver,
										 BOOL	bResumedAndCheck,
										 DeployStatusFunction deployStatusFunc); // callback function

typedef UINT (__cdecl *pfnStartToDeploy3)(wchar_t* szwAFDomain, // AF UI logon 
										 wchar_t* szwAFUserName, // AF UI logon
										 wchar_t* szwAFPassword, // AF UI logon
										 wchar_t* szwUUID,	  // UUID from JAVA side
										 wchar_t* szwServerName, // Remote server name
										 wchar_t* szwUserName,   // Log on User with administrator privilege
										 wchar_t* szwPassword,   // Password
										 UINT	 iPort,       // Port number
										 wchar_t* szwInstallDir,
										 BOOL   bAutoStartRRService,
										 BOOL	bIsReboot,
										 BOOL   bInstallDriver,
										 BOOL	bResumedAndCheck,
										 BOOL	bSwitchToHttps,
										 DeployStatusFunction deployStatusFunc); // callback function

typedef BOOL (__cdecl *pfnTestConnect) (BSTR szwAFDomain, // AF UI logon 
										BSTR szwAFUserName, // AF UI logon
										BSTR szwAFPassword, // AF UI logon
										BSTR szwServerName, // Remote server name
										BSTR szwUserName,   // Log on User with administrator privilege
										BSTR szwPassword,   // Password
										BSTR szwInstallPath,// installation path
										BOOL bAutoStartRRService,
										PD2DREGCONF pConfiguration, //installed D2D configuration 
										DWORD  * lpdwErr			//error code
										);

typedef BOOL (__cdecl *pfnTestConnect2) (BSTR szwAFDomain, // AF UI logon 
									   BSTR szwAFUserName, // AF UI logon
									   BSTR szwAFPassword, // AF UI logon
									   BSTR szwServerName, // Remote server name
									   BSTR szwUserName,   // Log on User with administrator privilege
									   BSTR szwPassword,   // Password
									   BSTR szwInstallPath,// installation path
									   BOOL bAutoStartRRService,
									   BOOL bInstallDriver, //insatll driver
									   PD2DREGCONF2 pConfiguration, //installed D2D configuration 
									   DWORD  * lpdwErr			//error code
									   );

/////////////////////////////////////////////////////////////////////////////////////////////
#define NO_PACKAGE  0 
#define X86_PACKAGE	1
#define X64_PACKAGE	2
#define ALL_PACKAGE	(X86_PACKAGE | X64_PACKAGE)
//Check if return value is NOT ALL_PACKAGE, then WebUI needs to show the warning
typedef UINT (__cdecl *pfnGetLocalPackage)();	//Get local deployment package status



#endif //AGENTDEPLOYT_H
