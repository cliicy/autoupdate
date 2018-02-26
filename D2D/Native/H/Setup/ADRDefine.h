#pragma once
// comapi.h : Defines the Registry and Path.
//

//key
#define NAME_CA								_T("CA")
#define NAME_FLASH							_T("CA ARCserve D2D")
#define NAME_FLASH_PATH						_T("ARCserve D2D") //without CA
#define NAME_INSTALLPATH					_T("InstallPath")
#define NAME_VERSION						_T("Version")
#define NAME_WEBSERVICE						_T("WebService")

#define REG_CA								_T("SOFTWARE\\") NAME_CA


//for old D2D(r15 ~ r16.5)
#define REG_ARCFLASH_ROOT					REG_CA _T("\\") NAME_FLASH
#define REG_ARCFLASH_PATH					REG_ARCFLASH_ROOT _T("\\") NAME_INSTALLPATH
#define REG_ARCFLASH_VERSION				REG_ARCFLASH_ROOT _T("\\") NAME_VERSION
#define REG_ARCFLASH_WEBSERVICE				REG_ARCFLASH_ROOT _T("\\") NAME_WEBSERVICE
//end //for old D2D

//common
#define NAME_ENGINE							_T("Engine")
#define NAME_MANAGEMENT						_T("Management")
#define NAME_CONSOLE						_T("Console")

//for Oolong Agent(5.0)
#define NAME_UDP_5							_T("ARCserve Unified Data Protection")
#define REG_UDP_ROOT_5						REG_CA _T("\\") NAME_UDP
#define REG_ENGINE_ROOT_5					REG_UDP_ROOT_5 _T("\\") NAME_ENGINE
#define REG_ENGINE_PATH_5					REG_ENGINE_ROOT_5 _T("\\") NAME_INSTALLPATH
#define REG_ENGINE_VERSION_5				REG_ENGINE_ROOT_5 _T("\\") NAME_VERSION
#define REG_ENGINE_WEBSERVICE_5				REG_ENGINE_ROOT_5 _T("\\") NAME_WEBSERVICE
#define NAME_ENGINE_PATH_5					NAME_UDP_5 _T("\\") NAME_ENGINE

//for Tungsten Agent(6.0 or later)
#define NAME_ARCSERVE						_T("Arcserve")
#define NAME_UDP							_T("Unified Data Protection")
#define REG_ARCSERVE						_T("SOFTWARE\\") NAME_ARCSERVE
#define REG_UDP_ROOT						REG_ARCSERVE _T("\\") NAME_UDP
#define REG_ENGINE_ROOT						REG_UDP_ROOT _T("\\") NAME_ENGINE
#define REG_ENGINE_PATH						REG_ENGINE_ROOT _T("\\") NAME_INSTALLPATH
#define REG_ENGINE_VERSION					REG_ENGINE_ROOT _T("\\") NAME_VERSION
#define REG_ENGINE_WEBSERVICE				REG_ENGINE_ROOT _T("\\") NAME_WEBSERVICE
#define NAME_ENGINE_PATH					NAME_UDP _T("\\") NAME_ENGINE

// Management
#define REG_MANAGEMENT_ROOT					REG_UDP_ROOT _T("\\") NAME_MANAGEMENT
#define REG_MANAGEMENT_CONSOLE				REG_MANAGEMENT_ROOT _T("\\") NAME_CONSOLE

//end for Oolong Agent

//log sub base path, the log path is like "C:\Winodws\Temp\" + the following sub base log path
#define DEFAULT_AGENT_LOG_SUBPATH				_T("Arcserve\\Setup\\UDP")

//setup common sub path is like "<programfile>\arcserve\SharedComponents\..."
#define DEFAULT_SETUPCOMMON_SUBPATH				_T("Arcserve\\SharedComponents\\arcserve Unified Data Protection\\Setup")

//Windows key
#define REG_WINDOWS_RUN		_T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run")
#define REG_WINDOWS_SERVICES _T("SYSTEM\\CurrentControlSet\\Services")
//value
#define REG_VALUE_PATH                      _T("Path")
#define REG_VALUE_PORT                      _T("Port")
#define REG_VALUE_MAJOR                     _T("Major")
#define REG_VALUE_MINOR                     _T("Minor")
#define REG_VALUE_BUILD                     _T("Build")
#define REG_VALUE_ADRTRAYICON               _T("ADRTrayIcon")
#define REG_VALUE_ADMINPASSWORD             _T("AdminPassword")
#define REG_VALUE_ADMINUSER					_T("AdminUser")
#define REG_VALUE_INSTALLSTATUS				_T("InstallStatus")
#define REG_VALUE_PACKAGETYPE				_T("PackageType")
#define REG_VALUE_GUID						_T("GUID")
#define REG_VALUE_LANGUAGEID				_T("LANGUAGEID")
#define REG_VALUE_INSTALLDRIVER				_T("InstallDriver")
#define REG_VALUE_URL						_T("URL")
#define REG_VALUE_PRODUCTTYPE				_T("ProductType")
#define REG_VALUE_MONITORFLAG				_T("MonitorFlag")
#define REG_VALUE_INSTALLUSER				_T("InstallUser")

//Service
#define SERVICE_ARCFLASHVOLDRV				_T("ARCFlashVolDrv")
#define ARCFLASH_SERVICE_ARCFLASHVOLDRV		_T("ARCFlashVolDrv")
#define ARCFLASH_SERVICE_AFFLT				_T("AFFlt")
#define SERVICE_ARCFLASHWEBSVC				_T("CASAD2DWebSvc")
#define SERVICE_SHPROVD				        _T("ShProvd")
#define SERVICE_CASUNIVERSALAGENT		    _T("CASUniversalAgent")
#define SERVICE_AFSTORHBA				    _T("AFStorHBA")    //CA ARCserve D2D Mount Driver service
#define SERVICE_DATASTORESERVICE			_T("CASDatastoreSvc")  //for GDD
#define SERVICE_REPLICATIONSVC			    _T("CASReplicationSvc")  //for Replication service
#define SERVICE_CASARPSWEBSVC			    _T("CASARPSWebSvc")  // for RPS web service
#define SERVICE_CAARCUPDATESVC				_T("CAARCUpdateSvc") //for update service
//end service

//Driver registry
#define REG_SERVICE_ARCFLASHVOLDRV			REG_WINDOWS_SERVICES _T("\\") SERVICE_ARCFLASHVOLDRV

//service key string for replacement
#define KEY_PR_SERVICENAME			_T("<SERVICE_NAME>")
#define KEY_PR_DISPLAYNAME			_T("<PR_DISPLAYNAME>")
#define KEY_PR_DESCRIPTION			_T("<PR_DESCRIPTION>")
			
//Path
#define ADT_PATH						_T("Packages\\D2D")
#define ADT_PATH_INSTALL				ADT_PATH _T("\\Install")
#define DEFAULT_INSTALL_PATH			NAME_CA _T("\\") NAME_ENGINE_PATH

//doc path in DVD
#define DOC_SOURCEPATH					_T("D2D\\Doc")

//doc install path
#define DOC_INSTALLPATH				   _T("TOMCAT\\webapps\\ROOT\\Doc")

//D2D web UI files
#define D2DUI_SOURCEPATH					_T("D2D\\Common\\D2DUI\\contents")
#define D2DUI_INSTALLPATH					_T("TOMCAT\\webapps\\ROOT\\contents")


//JRE path
#define JRE_SOURCEPATH_X64				_T("Common\\JRE\\x64")
#define JRE_SOURCEPATH_X86				_T("Common\\JRE\\x86")

#define JRE_INSTALLPATH_TOMCAT			_T("TOMCAT\\JRE")
#define JRE_INSTALLPATH_WINPE_AMD64		_T("BIN\\DR\\WinPE\\AMD64\\JRE")
#define JRE_INSTALLPATH_WINPE_X86		_T("BIN\\DR\\WinPE\\X86\\JRE")
//end JRE 

//VDDK

//only for previous D2D
#define VDDK_SOURCEPATH_BASE			_T("Common\\VDDK")
#define VDDK_SOURCEPATH_X64				VDDK_SOURCEPATH_BASE _T("\\x64")
#define VDDK_SOURCEPATH_X86				VDDK_SOURCEPATH_BASE _T("\\x86")

//for Oolong
#define VDDK_SOURCEPATH_BASE_EX			_T("D2D\\Common\\VDDK")
#define VDDK_SOURCEPATH_X64_EX				VDDK_SOURCEPATH_BASE_EX _T("\\x64")
#define VDDK_SOURCEPATH_X86_EX				VDDK_SOURCEPATH_BASE_EX _T("\\x86")

#define VDDK_INSTALLPATH_X86			_T("BIN\\VDDK")
#define VDDK_INSTALLPATH_X64			_T("BIN\\VDDK\\BIN\\VDDK64")
#define VDDK_INSTALLPATH_WINPE_AMD64	_T("BIN\\DR\\WinPE\\AMD64\\VDDK")
#define VDDK_INSTALLPATH_WINPE_X86		_T("BIN\\DR\\WinPE\\X86\\VDDK")
//end VDDK


/**************************** starrt icf define *********************/
//section for setup.icf
#define ICF_SECTION_ADR					_T("ARCFlash")
#define ICF_SECTION_INSTALL				_T("Install")
#define ICF_SECTION_SETUP				_T("Setup")
#define ICF_SECTION_UPGRADE				_T("Upgrade")

//for Oolong
#define ICF_SECTION_D2D				    _T("Agent")
#define ICF_SECTION_RPS				    _T("Server")
//end section 

//value for setup.icf
#define ICF_VALUDE_INSTALLDIR			_T("INSTALLDIR")
#define ICF_VALUDE_PORTNUMBER			_T("PortNumber")
#define ICF_VALUDE_UUID					_T("UUID")
#define ICF_VALUDE_AFUSER				_T("AFUser")
#define ICF_VALUDE_AFPWD				_T("AFPassword")
#define ICF_VALUDE_FIREWALL				_T("CallFirewall")
#define ICF_VALUDE_OLDMAJORVERSION		_T("OldMajorVersion")
#define ICF_VALUDE_OLDMINORVERSION		_T("OldMinorVersion")
#define ICF_VALUDE_OLDMAJORBUILD		_T("OldMajorBuild")
#define ICF_VALUDE_INSTALLDRIVER		_T("InstallDriver")
#define ICF_VALUDE_SWITCHTO_HTTPS		_T("SwitchToHttps")
#define ICF_VALUE_PRODUCTTYPE			_T("ProductType")
#define ICF_VALUE_MONITORFLAG			_T("MonitorFlag")
#define ICF_VALUE_LOG_DIR				_T("LOGFolder")
#define ICF_VALUE_SETUP_ERROR			_T("SetupError")
#define ICF_VALUE_PORTRANGEFORGDD		_T("PortRangeForGDD")
#define ICF_VALUE_COMMPORT				_T("COMMPORT")
#define ICF_VALUE_RPS_WEBSERVICE_PORT	_T("WebServicePort")
#define ICF_VALUE_RPS_PORTSHARING_FLAG	_T("UsePortSharing")
#define ICF_VALUE_RPS_INTERNALPORT	    _T("InternalPort")
#define ICF_VALUE_RPS_ISSHAREDSOCKET	_T("IsSharedSocket")
//end value

/**************************** end icf define *********************/

//default port range for GDD
#define DEFAULT_PORTRANGEFORGDD			_T("5000-5060")

//default port value for Replication service
#define DEFAULT_RPS_COMMPORT		    _T("7788")

//default port value for RPS web service
#define DEFAULT_RPS_WEBSERVICE_PORT		    _T("80")

//internal port value for D2D web service when RPS is installed
#define DEFAULT_RPS_INTERNAL_PORT		    _T("8016")

//port sharing flag
#define RPS_PORTSHARING_FLAG_DEFAULT		_T("0")
#define RPS_PORTSHARING_FLAG_USE			_T("1")

//Product Type
#define PRODUC_TYPE_GENERAL				0
#define PRODUC_TYPE_SAAS				1
#define PRODUC_TYPE_RPS					2

//web value
#define WEB_VALUE_PORT					_T("8014")
#define DEFAULT_WEB_PORT				8014

//Short Name
#define PRODUCT_ARCD2D_X86				_T("ARCD2DX86")
#define PRODUCT_ARCD2D_X64				_T("ARCD2DX64")

//Log folder
#define PATH_LOGS						_T("Logs")

#define PATH_LOGS_DEPLOYMENT			PATH_LOGS _T("\\") _T("Deployment")

//file
#define FILE_ADTCHECKSUM				_T("ADTFilesSum.sum")
#define FILE_LOGFILE					_T("LogFile.ini")

//reboot flag file, when the reboot is required during install/uninstall.
//this file will be created under windows temp folder.Also added to file pending removal(HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\PendingFileRenameOperations).
#define REBOOT_FLAG_FILE _T("as_reboot_d2d.ini")

//package (full package x86|x64)
#define PACKAGE_TYPE_X86				1
#define PACKAGE_TYPE_X64				2

//Deploy request minimum size
#define DEPLOY_MIN_DISK_SIZE			306 //MB

/*InstallStatus Registry value
1  -fresh installation with reboot.
2  -fresh installation without reboot.
3  -Upgrade with reboot.
4  -Upgrade without reboot.
*/
#define STATUS_FRESHINSTALL_REBOOT		1
#define STATUS_FRESHINSTALL_NOREBOOT	2
#define STATUS_UPGRADE_REBOOT			3
#define STATUS_UPGRADE_NOREBOOT		    4

//for deployment tool
#define SETUP_DEPLOYMENT_MUTEX		_T("PreventBABMasterSetupInstance")

//for mastersetup main
#define SETUP_COMMON_MUTEX			_T("PreventBABMasterSetupInstance")


//for mastersetup.exe: default setup.icf for silent mode
#define DEFAULTSILENT_SETUPICF_X86  _T("..\\IntelNT\\ADR\\setup.icf")
#define DEFAULTSILENT_SETUPICF_X64	_T("..\\X64\\ADR\\setup.icf")
//end silent

#define FILE_MASTERSETUP_INF		_T("mastersetup.inf")

//for setup and patch
#define SETUPPATCH_COMMON_PATH		_T("Setup")
//end setupp and patch

//for mastersetup.inf
#define INF_WOW64CHECK_SEC			_T("WOW64Check")
#define INF_WOW64CHECK_REGENTRY		_T("RegEntry")
#define INF_WOW64CHECK_REGVALUE		_T("RegValue")
#define INF_UPDATE_CONFIG_FILE		_T("UpdateConfigFile")
#define KEY_UPDATE_INSTALLDIR		_T("<INSTALLDIR>")
#define KEY_UPDATE_PATCHMGR			_T("<PATCH_MANAGER_PATH>")
#define INF_SECTION_FOLDERCOPY		_T("ImageFolderCopy")

/**************************** Update define *********************/

//registry value
#define REG_VALUE_PATCHVERSIONNUMBER		_T("UpdateVersionNumber")

//Update path  (like <installdir>Updates)
#define PATCH_PATH					_T("Updates")	

//Update manager path  (like <installdir>Update Manager)
#define PATCH_MANAGER_PATH			_T("Update Manager")

#define DEFAULT_LANGUAGEID			_T("1033")

#define HOTFIXREMOVE_YES			_T("YES")

/**************************** starrt property define *********************/

#define PROP_D2DUPGRADE				_T("D2DUPGRADE")
#define PROP_REBOOT					_T("D2DREBOOT")
#define PROP_UNINSTALLDRIVER		_T("UNINSTALLDRIVER")
#define PROP_PACKAGETYPE			_T("PACKAGETYPE")
#define PROP_CDIMAGE			    _T("CDIMAGE")
#define PROP_COREOS					_T("COREOS")
#define PROP_INSTALLDRIVERFLAG		_T("INSTALLDRIVERFLAG")
#define PROP_UASTATUS				_T("UASTATUS")   //1 stop by uninstallation
#define PROP_ISSHAREDSOCKET			_T("ISSHAREDSOCKET")
#define PROP_ISENABLEDSSL			_T("ISENABLEDSSL")
#define PROP_UASTATUS				_T("UASTATUS")   //1 stop by uninstallation
#define PROP_PATCHERRORMSG			_T("PATCHERRORMSG")
#define PROP_LANGUAGEID				_T("LANGUAGEID")
#define PROP_UPDATEDLL				_T("UPDATEDLL")
#define PROP_PRODUCTTYPE			_T("PRODUCTTYPE")
#define PROP_OLDMAJORVERSION		_T("OLDMAJORVERSION")
#define PROP_OLDMINORVERSION		_T("OLDMINORVERSION")
#define PROP_OLDMAJORBUILD		    _T("OLDMAJORBUILD")
#define PROP_ICFPATH				_T("ICFPATH")
#define PROP_MONITORFLAG		    _T("MONITORFLAG")
#define PROP_REBOOTMSG		        _T("REBOOTMSG")
#define PROP_PORTRANGEFORGDD		_T("PORTRANGEFORGDD")
#define PROP_REPLICATIONSVCPORT		_T("REPLICATIONSVCPORT")
#define PROP_RPSWEBSERVICEPORT		_T("RPSWEBSERVICEPORT")
#define PROP_USEPORTSHARING		    _T("USEPORTSHARING")
#define PROP_INTERNALPORT		    _T("INTERNALPORT")
#define PROP_RPSUPGRADE				_T("RPSUPGRADE")
#define PROP_AOSMAJORVERSION		_T("AOSMAJORVERSION")
#define PROP_AOSMINORVERSION		_T("AOSMINORVERSION")

//for test fix, hotfix remove  (1 yes, non-1, no)
#define PROP_HOTFIXUPDATEREMOVE		_T("HOTFIXUPDATEREMOVE")

//update msi dll (like <installdir>Updates\UpdateMsi.dll)
#define PROP_PATCHDLLFILE		   _T("UPDATEDLLFILE")

//update res file (like <installdir>Updates\UpdateRes.ini)
#define PROP_PATCHRESINIFILE		_T("UPDATERESINIFILE")

/**************************** end property define *********************/


//default update msi dll name, in fact, user need add the property "UPDATEDLL" into ISM file
#define PATCHMSI_DLL		_T("UpdateMsi.dll")
#define PATCHRES_INI		_T("UpdateRes.ini")
#define PATCHHISTORY_XML	_T("MSPUpdateHistory.XML")

#define PATCH_LOG_PRENAME  _T("UpdateMsi")

//xml tag
#define XML_TAG_UPDATES			_T("Updates")
#define XML_TAG_UPDATE			_T("Update")

//The flag that UA is using D2D
#define MUTEX_UA_SETUP		_T("Global\\CA_ARC_BKP_UA_SETUP_20B441EB-8798-468d-A69C-C14C15730F52")

//Run D2D Monitor for:
#define RUN_FOR_ALL_USER			0
#define RUN_FOR_CURRENT_USER		1

//Setup.exe error
#define MSI_ERROR_SERVICE					0X01
#define MSI_ERROR_DRIVER_CHANGETRACKING		0X02
#define MSI_ERROR_DRIVER_KMDF				0X04
#define MSI_ERROR_DRIVER_UMDF				0X08
#define MSI_ERROR_DRIVER_INSTANTVM			0X10
/**************************** End Update define *****************/
