#pragma once

#include <string>

using namespace std;

#define REG_EDGE_ROOT			L"SOFTWARE\\CA\\CA ARCserve Central Applications"
#define REG_EDGE_CM				L"SOFTWARE\\CA\\CA ARCserve Central Applications\\CM"
#define REG_EDGE_VCM			L"SOFTWARE\\CA\\CA ARCserve Central Applications\\VCM"
#define REG_EDGE_VSPHERE		L"SOFTWARE\\CA\\CA ARCserve Central Applications\\VSphere"
#define REG_EDGE_REPORT			L"SOFTWARE\\CA\\CA ARCserve Central Applications\\Report"
#define REG_EDGE_COMMON			L"SOFTWARE\\CA\\CA ARCserve Central Applications\\WebServer"
#define REG_UDP_CONSOLE			L"SOFTWARE\\Arcserve\\Unified Data Protection\\Management"
#define REG_EDGE_UPDATEMANAGER	L"Update Manager"
#define REG_EDGE_PATH			L"Path"
#define REG_EDGE_RestartServiceAfterPatch			L"RestartServiceAfterUpdate"
#define REG_EDGE_UpdateVersionNumber				L"UpdateVersionNumber"
#define REG_EDGE_UpdateExitCode						L"UpdateExitCode"


#define APM_EDGE_COMMON			1
#define APM_EDGE_CM				2
#define APM_EDGE_VCM			4
#define APM_EDGE_VSPHERE		8
#define APM_EDGE_REPORT			16
#define APM_D2D					32
#define UDP_CONSOLE				64

#define SERVICE_ARCAPP								L"CAArcAppSvc"
#define SERVICE_AGENT                               L"CASAD2DWebSvc"

#define FOLDER_LOG									L"Logs"
#define FOLDER_UPDATEMANAGER						L"Update Manager"
#define FOLDER_UPDATEMANAGER_ARCAPP					L"ArcApp"
#define FOLDER_UPDATEMANAGER_CM						L"CM"
#define FOLDER_UPDATEMANAGER_COMMON					L"Common"
#define FOLDER_UPDATEMANAGER_REPORTING				L"Reporting"
#define FOLDER_UPDATEMANAGER_VCM					L"VCM"
#define FOLDER_UPDATEMANAGER_VSphere				L"VSphere"
#define FOLDER_BIN									L"bin"

#define FILE_UPDATEMANAGER_STATUS					L"Status.xml"
#define FILE_ARCAPPUPDATEMANGER_EXE					L"ArcAppUpdateManager.exe"
#define FILE_ARCD2DUPDATEMANGER_EXE					L"D2DUpdateManager.exe"
#define FILE_EdgePMCommandBase_dll					L"EdgePMCommandBase.dll"
#define FILE_D2DPMCOMMANDBASE_DLL                   L"D2DPMCommandBase.dll"
#define FILE_AFCOREINTERFACE_DLL					L"AFCoreInterface.dll"
#define FILE_EDGE_PMSETTING_INI						L"PMSettings.INI";
#define FILE_D2D_PMSETTING_INI						L"D2DPMSettings.INI";
#define FILE_EDGE_PMClient_XML						L"PMClient.xml"
#define FILE_D2D_PMCLIENT_XML                       L"D2DPMClient.xml"

const int PRODUCTS[5] = {APM_EDGE_COMMON, APM_EDGE_CM, APM_EDGE_REPORT, APM_EDGE_VCM, APM_EDGE_VSPHERE};
#define PRODUCT_COUNT				(sizeof(PRODUCTS)/sizeof(int))



BOOL	IsProductInstalled( int nProductId );

DWORD	GetRegRootKeyByProductId( int nProductId, wstring &strRegKey );

//strDir "C:\Program Files\CA\ARCserve Unified Data Protection\"
DWORD	GetEdgeRootDir( wstring &strDir );

//if the key does not exist, strValue is an empty string
//return -1: general error; -2: invalid key path
DWORD	GetProductExtentionKey( int nProductId, const wstring &strKeyName, wstring &strValue );

//if the key does not exist, strValue is an empty string
//return -1: general error; -2: invalid key path
DWORD	SetProductExtentionKey( int nProductId, const wstring &strKeyName, const wstring &strValue );
