#pragma once
#include "ARCUpdate.h"
#include <string>
#include <vector>
using namespace std;

//--------------------------------------------------
#define STRING(x)          L##x
#define WSTRING(x)         STRING(x)
#define __WFUNCTION__      WSTRING(__FUNCTION__)

#define SAFE_DELETE(p)  { if(p)  {delete p; p=NULL;    } }
#define SAFE_RELEASE(p) { if(p)  {p->Release(); p=NULL;} }
#define SAFE_FREE(p)    { if(p)  {free(p); p=NULL;     } }


#define	HTTP_CLIENT_NAME							L"UDP_AutoUpdate"
#define AVAILABLE_UPDATE_INFO_DLL					L"AvailableUpdateInfo.dll"
#define AVAILABLE_PATCH_INFO_DLL					L"AvailablePatchInfo.dll"
#define AVAILABLE_PATCHBINARY_DLL					L"AvaiablePatchBinary.dll"
#define BINARY_PATCH_DIR							L"_Binary"


//--------------------------------------------------
//
#define	IDR_XML1									101 // the resource ID of AvailableUpdateInfo.xml


//--------------------------------------------------
//
#define DIR_UPDATE_MANAGER							L"Update Manager"
#define XML_FILE_UPDATE_URLS     					L"UpdateURL.xml"
#define XML_FILE_UPDATE_HISTORY						L"DownloadHistory.xml"
#define XML_FILE_STATUS								L"Status.xml"
#define XML_BINARY_STATUS							L"Binary_Status.xml"
#define XML_UpdateBIJobScript						L"UpdateBIJobScript"


//
// XML definations for update job script
//
#define XML_UpdateJobScript							L"UpdateJobScript"
#define XML_PreferenceFile							L"PreferenceFile"
#define XML_DownloadServer							L"DownloadServer"
#define XML_type									L"type"
#define XML_MailAlert								L"MailAlert"
#define XML_IgnoreVersionCheck						L"IgnoreVersionCheck"
#define XML_Proxy									L"Proxy"
#define XML_DownloadDir								L"DownloadDir"
#define XML_AdminUser								L"AdminUser"
#define XML_AdminPassword							L"AdminPassword"
//
// XML definations in PreferencesConfiguration.xml
//
#define XML_AutoUpdateSettings					    L"autoUpdateSettings"
#define XML_backupsConfigured						L"BackupsConfigured"
#define XML_ScheduleType							L"scheduleType"
#define XML_ScheduledHour							L"ScheduledHour"
#define XML_ScheduledWeekDay						L"ScheduledWeekDay"
#define XML_ScheduledMinute							L"ScheduledMinute"
#define XML_StagingServerSettings					L"StagingServerSettings"
#define XML_StagingServers							L"StagingServers"
#define XML_StagingServer							L"stagingServer"
#define XML_StagingServerPort						L"stagingServerPort"
#define XML_stagingServerStatus						L"stagingServerStatus"
#define XML_ProxySettings							L"proxySettings"
#define XML_useProxy								L"useProxy"
#define XML_ProxyServerName							L"proxyServerName"
#define XML_ProxyServerPort							L"proxyServerPort"
#define XML_ProxyUserName							L"proxyUserName"
#define XML_ProxyPassword							L"proxyPassword"
#define XML_serverType								L"serverType"
#define XML_BIserverType							L"BIserverType"
#define XML_serverId								L"serverId"
#define XML_iCAServerStatus							L"iCAServerStatus"
#define XML_proxyRequiresAuth						L"proxyRequiresAuth"

//
// xml defininations for checking update
//
#define XML_updatejob								L"updatejob"
#define XML_httpsettings							L"httpsettings"

//
// XML definations in AvailableUpdateInfo.xml
//
#define XML_Product									L"Product"
#define XML_TriggerProduct							L"TriggerProduct"
#define XML_SchemaVersion							L"SchemaVersion"
#define XML_Package									L"Package"
#define XML_Desc									L"Desc"
#define XML_UpdateURL								L"UpdateURL"
#define XML_UpdateInfo								L"UpdateInfo"
#define XML_BaseBuild								L"BaseBuild"
#define XML_ReleaseNotes							L"ReleaseNotes"
#define XML_Code									L"Code"
#define XML_Release									L"Release"
#define XML_MajorVersion							L"MajorVersion"
#define XML_MinorVersion							L"MinorVersion"
#define XML_ServicePack								L"ServicePack"
#define XML_Update									L"Update"
#define XML_Path									L"Path"
#define XML_Size									L"Size"
#define XML_Checksum								L"Checksum"
#define XML_DownloadedOn							L"DownloadedOn"
#define XML_InstallStatus							L"InstallStatus"
#define XML_RebootRequired							L"RebootRequired"
#define XML_LastRebootableUpdateVersion				L"LastRebootableUpdateVersion"
#define XML_RequiredVersionOfAutoUpdate				L"RequiredVersionOfAutoUpdate"
#define XML_UpdateVersionNumber						L"UpdateVersionNumber"
#define XML_UpdateBuild								L"UpdateBuild"
#define XML_DownloadHistory							L"DownloadHistory"
#define XML_PublishedDate							L"PublishedDate"
#define XML_Id										L"Id"
#define XML_Downloadedlocation						L"Downloadedlocation"
#define XML_DownloadStatus							L"DownloadStatus"
#define XML_AvailableStatus							L"AvailableStatus"
#define XML_Flags									L"Flags"
#define XML_SrcFileURL								L"SourceFileURL"
#define XML_DstFilePath								L"DestFilePath"
#define XML_Sync									L"Sync"
#define XML_Order									L"Order"
#define XML_Command									L"Command"
#define XML_WorkingDir								L"WorkingDir"
#define XML_PostDownloadActions						L"PostDownloadActions"
#define	XML_Action									L"Action"
#define	XML_UpdateFiles								L"UpdateFiles"
#define	XML_File									L"File"
#define	XML_Name									L"Name"


#define XML_NODEPATH_Release						L"\\Release"
#define XML_NODEPATH_Package						L"\\Release\\Package"
#define XML_NODEPATH_path							L"\\Release\\Package\\Path"
#define XML_NODEPATH_Update							L"\\Release\\Package\\Update"
#define XML_NODEPATH_CheckSum						L"\\Release\\Package\\CheckSum"
#define XML_NODEPATH_Size							L"\\Release\\Package\\Size"
#define XML_NODEPATH_SizeInBytes					L"\\Release\\Package\\SizeInBytes"
#define XML_NODEPATH_RebootRequired					L"\\Release\\Package\\RebootRequired"
#define XML_NODEPATH_LastRebootableUpdateVersion	L"\\Release\\Package\\LastRebootableUpdateVersion"
#define XML_NODEPATH_UpdateVersionNumber			L"\\Release\\Package\\UpdateVersionNumber"
#define XML_NODEPATH_UpdateBuild					L"\\Release\\Package\\UpdateBuild"
#define XML_NODEPATH_Downloadedlocation				L"\\Release\\Package\\Downloadedlocation"
#define XML_NODEPATH_InstallStatus					L"\\Release\\Package\\InstallStatus"
#define XML_NODEPATH_UpdateFiles					L"\\Release\\Package\\UpdateFiles"
#define XML_NODEPATH_PostDownloadActions			L"\\Release\\Package\\PostDownloadActions"

//
// defines in updateURL.xml
//
#define XML_PathOnSource							L"Pathonsource"
#define XML_Port									L"Port"
#define XML_PatchBI_OnSrc							L"binary_onsource"

//added by cliicy.luo
#define XML_NODESUB_path							L"Path"
#define XML_NODESUB_Update							L"Update"
#define XML_NODESUB_CheckSum						L"CheckSum"
#define XML_NODESUB_Size							L"Size"
#define XML_NODESUB_SizeInBytes						L"SizeInBytes"
#define XML_NODESUB_RebootRequired					L"RebootRequired"
#define XML_NODESUB_LastRebootableUpdateVersion		L"LastRebootableUpdateVersion"
#define XML_NODESUB_UpdateVersionNumber				L"UpdateVersionNumber"
#define XML_NODESUB_UpdateBuild						L"UpdateBuild"
#define XML_NODESUB_Downloadedlocation				L"Downloadedlocation"
#define XML_NODESUB_InstallStatus					L"InstallStatus"
#define XML_NODESUB_UpdateFiles						L"UpdateFiles"
#define XML_NODESUB_PostDownloadActions				L"PostDownloadActions"
//added by cliicy.luo

//
// for UDP Patch
//
#define XML_Patches									L"Patches"
#define XML_Patch									L"Patch"
#define XML_PatchFiles								L"PatchFiles"
#define XML_Dependency								L"Dependency"
#define XML_Builds									L"Builds"
#define XML_Build									L"Build"
#define XML_Version									L"Version"

//
// the registry key 
//
#define	REG_K_VERSION								L"Version"
#define REG_K_CONSOLE								L"Console"
#define REG_K_MAJOR									L"Major"
#define REG_K_MINOR									L"Minor"
#define REG_K_SERVICE_PACK							L"ServicePack"
#define REG_K_UPDATE								L"UpdateVersionNumber"
#define REG_K_UPDATE_BUILD							L"UpdateBuildNumber"
#define REG_K_BUILD									L"Build"
#define REG_K_ROOTPATH								L"RootPath"
#define REG_K_GATEWAYFLAG							L"GatewayFlag"

//
// the CA update server information. 
// Defined in \Update Manager\UpdateURL.xml
//
typedef struct _CA_UPDATE_SERVER_INFO
{
	wstring		strServerName;	
	wstring		strPathOnSource;
	wstring		str_Binary_PathOnSource;
	int			nPort;

	_CA_UPDATE_SERVER_INFO()
	{
		strServerName = L"";		
		strPathOnSource = L"";
		str_Binary_PathOnSource = L"";
		nPort = 80;
	}
}CA_UPDATE_SERVER_INFO, *PCA_UPDATE_SERVER_INFO;

//
// the product version information
//
typedef struct _UDP_VERSION_INFO_
{
	DWORD dwMajor;		   // the major version
	DWORD dwMinor;		   // the minor version
	DWORD dwServicePack;   // the service pack
	DWORD dwBuild;		   // the build number
	DWORD dwUpdate;		   // the update number
	DWORD dwUpBuild;	   // the update build number
	_UDP_VERSION_INFO_()
	{
		dwMajor = 0;
		dwMinor = 0;
		dwServicePack = 0;
		dwBuild = 0;
		dwUpdate = 0;
		dwUpBuild = 0;
	}
}UDP_VERSION_INFO, *PUDP_VERSION_INFO;


//
// the update settings
//
// define the staging server information
typedef struct _UDP_STAGING_SVR
{
	wstring	strServerName;
	int		nPort;
	int		nIndex;
	_UDP_STAGING_SVR( )
	{
		strServerName = L"";
		nPort = 8014;
		nIndex = 0;
	}
}UDP_STAGING_SVR, *PUDP_STAGING_SVR;

typedef struct _UDP_UPDATE_SCHEDULE
{
	BOOL		bDisabled;	 // autoupate is disabled or not
	int			nDay;		 // 0: every day
	int			nHour;
	int			nMinute;
	_UDP_UPDATE_SCHEDULE()
	{
		bDisabled = FALSE;
		nDay = 1;
		nHour = 3;
		nMinute = 0;
	}
}UDP_UPDATE_SCHEDULE, *PUDP_UPDATE_SCHEDULE;


//
// the proxy info used to download update
//
typedef struct _ARCUPDATE_PROXY
{
	BOOL	bDefaultIEProxy;// default proxy, same as IExplorer
	int		nProxyPort;		// the proxy server port;
	wstring proxyServer;	// the proxy server name
	wstring proxyUserName;  // the proxy server user name
	wstring proxyPassword;	// the proxy server password

	_ARCUPDATE_PROXY()
	{
		bDefaultIEProxy = TRUE;
		nProxyPort = 80;
		proxyServer = L"";
		proxyUserName = L"";
		proxyPassword = L"";
	}
}ARCUPDATE_PROXY, *PARCUPDATE_PROXY;

typedef struct _UDP_UPDATE_SETTINGS_
{
	int							nServerType;		// 0: from CA server. 1: from stageing erver
	int							nBIServerType;		// 0: from CA server. 1: from stageing erver
	UDP_UPDATE_SCHEDULE			scheduler;			// the update scheduler
	ARCUPDATE_PROXY				ieProxy;			// the proxy infor
	vector<UDP_STAGING_SVR>		vecStagingServers;	// the staging servers.
	_UDP_UPDATE_SETTINGS_()
	{
		nServerType = ARCUPDATE_SERVER_DEFAULT;
		vecStagingServers.clear();
	}
}UDP_UPDATE_SETTINGS, *PUDP_UPDATE_SETTINGS;
