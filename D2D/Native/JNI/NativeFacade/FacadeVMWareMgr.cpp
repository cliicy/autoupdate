#include "stdafx.h"
#include <stdio.h>
#include <stdlib.h>
#include <tchar.h>
#include <atlstr.h> //<sonmi01>2012-8-22 ###???
#include "../../Virtualization/Common/VmwareMgr/VMwareVMTask.h"
#include "HAClientProxy.h"
#include "HyperVRepCallBack.h"
#include "..\..\Virtualization\VirtualStandby\HaUtility\ComEnvironment.h"
#include "FacadeVMWareMgr.h" //<sonmi01>2012-8-23 #new struct
//#include "p2v_controller.h" //<sonmi01>2012-8-23 ###???
#include "rvcm_save_network_mapping_to_file.h"
#include "IniDefine.h"
#include "CopyFileTree.h"
#include "utils.h"
#include <string> //<sonmi01>2012-8-22 ###???
#include <vector>
#include <fstream>
#include "UDPResource.h"
#include "UDPMessage.h"
#include "HaCommonFunc.h"


using namespace std;

#ifndef HANativeLogMsg
#define HANativeLogMsg _tprintf_s
#endif

#ifndef BEGIN_BLOCK
#define BEGIN_BLOCK(level_number)			do{	int	_abc_xyz##level_number(level_number);
#define END_BLOCK(level_number)				{_abc_xyz##level_number;}	}while(0);
#endif


#define COMPRESSED 0
#define UNCOMPRESSED_PARENT 1
#define UNCOMPRESSED_CHILD 2

#define D2D2VMDK_SUCCESS	0
#define D2D2VMDK_FAIL	1

static HRESULT PrepareServiceAndToolFiles(HMODULE hMountVMMgr, wstring & DestDir, bool isX86, const wstring& scriptPath); //<sonmi01>2012-8-22 ###???
static VOID AdptorInfoVectorW2A(CONST vector<Adapter_Map_InfoW> & wInfo, vector<NSRVCM::adapter_map_info> & aInfo); //<sonmi01>2012-8-23 ###???

typedef DWORD(WINAPI *PFN_D2D2VmdkConversion) (const wstring& vhdFileName, const wstring& moref, const wstring& hostName, const wstring& userName, const wstring& password, const wstring& diskUrl, int vddkPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, int diskType, const wstring& snapMoref, const wstring& jobID, const wstring& VMUniqueID, unsigned long blockSize, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, unsigned long &errorCode, unsigned long &d2dErrorCode, IHADTCallback* pCallback);
typedef DWORD(WINAPI *PFN_D2D2VmdkSmartCopy) (const wstring& D2DFilePathBegin, const wstring& D2DFilePathEnd, const wstring& moref, const wstring& hostName, const wstring& userName, const wstring& password, const wstring& diskUrl, int vddkPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, int diskType, const wstring& snapMoref, const wstring& jobID, const wstring& VMUniqueID, unsigned long blockSize, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, unsigned long &errorCode, unsigned long &d2dErrorCode, IHADTCallback* pCallback);
typedef DWORD(WINAPI *PFN_UpdateDiskSigViaNBD) (const wstring& vhdFileName, const wstring& moref, const wstring& hostName, const wstring& userName, const wstring& password, const wstring& diskUrl, int vddkPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, int diskType, const wstring& snapMoref, const wstring& jobID, const wstring& VMUniqueID, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, IHADTCallback* pCallback);
typedef DWORD (WINAPI *PFN_InstallVMwareTools)();
typedef BOOL (WINAPI *PFN_GetGuestID)(wstring& strGuestID);
typedef DWORD (WINAPI *PFN_InstallVMwareTools)();

//modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.
typedef DWORD(WINAPI *PFN_GetSignature)(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strSnapMoref, const wstring& strVMDKURL, wstring& strDskGUID, const wstring& strJobID, const wstring& strAfguid);
typedef DWORD(WINAPI *PFN_DriverInject)(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, const VMwareVolumeInfo& volInfo, const wstring& newHostName, const wstring& szFailoverMode, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, std::vector<std::wstring> strVMDKURL, const wstring& key, const wstring& value, const wstring& JOBId, const wstring& strAfguid, bool isUEFI, const wstring& wstrDrzFilePath);
typedef DWORD(WINAPI *PFN_DriverInjectSingleVMDK)(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, const wstring& newHostName, const wstring& szFailoverMode, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strVMDKURL, const wstring& key, const wstring& value, const wstring& JOBID, const wstring& strAfguid);
typedef DWORD(WINAPI *PFN_SetVMDKGeometry)(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strVMDKURL, long volOffset, const wstring& strJobID, const wstring& strAfguid);
//<sonmi01>2012-8-22 ###???
typedef DWORD(WINAPI *PFN_RVCMInjectService)(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, const VMwareVolumeInfo& volInfo,
	const wstring& szFailoverMode, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, std::vector<std::wstring> strVMDKURL, const wstring& szFileDirInKey, //reuse key field as file path
	const wstring& JOBID);
//modify end

typedef bool (WINAPI *PFN_D2DCancelConversion)(const wstring& JobID);
typedef DWORD (WINAPI *PFN_GetVddkVersion)(DWORD& nVerHigh, DWORD& nVerLow, DWORD& nVerSub);
typedef DWORD (WINAPI *PFN_HA_LockD2DSessions)(/*IN*/const wchar_t* pwszSessRoot, /*IN*/int nSessBegin, /*IN*/int nSessEnd, /*OUT*/HANDLE* phLock, /*IN*/BOOL bRemoteVCM);
typedef void  (WINAPI *PFN_HA_UnlockD2DSessions)(HANDLE hLock);
typedef DWORD (WINAPI *PFN_HA_UnlockCTF2)(HANDLE hLock);




DWORD ConvertVHD2VMDK(const wstring& afGUID, const wstring& VHDFileName, const wstring& moref, const wstring& hostName, 
					  const wstring& userName, const wstring& password, const wstring& diskUrl,
					  int vddkPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, int diskType, const wstring& snapMoref, const wstring& jobID,
					  unsigned long blockSize, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, 
					  unsigned long &errorCode, unsigned long &d2dErrorCode, IHADTCallback* callback)
{
	PFN_D2D2VmdkConversion pfnD2D2VmdkConversion = NULL;
	// Each function will load a new instance of vmjob.dll.This can avoid concurrent issue in monitor and monitee.
	HMODULE hModule = LoadLibrary(_T("vmjob.dll"));
	DWORD m_ret = 1;
	
	if(hModule)
	{
		pfnD2D2VmdkConversion = (PFN_D2D2VmdkConversion)GetProcAddress(hModule, "D2D2VmdkConversion");
		if(pfnD2D2VmdkConversion != NULL)
		{
			m_ret = (*pfnD2D2VmdkConversion)(VHDFileName, moref, hostName, userName, password, diskUrl,
				vddkPort, moreParams, diskType, snapMoref, jobID, afGUID, blockSize, nBackupDescType, NetConnUserName, NetConnPwd, errorCode, d2dErrorCode, callback);
		}
		
		if(hModule != NULL)
		{
			FreeLibrary(hModule);
			hModule = NULL;
		}
		
	}	

	return m_ret;

}

DWORD FacadeD2D2VmdkSmartCopy(const wstring& afGUID, const wstring& D2DFilePathBegin, const wstring& D2DFilePathEnd,
							  const wstring& moref, const wstring& EsxHostName, const wstring& EsxUserName, 
							  const wstring& EsxUserPassword, const wstring& VmdkDiskUrl,
							  DWORD VDDKPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, DWORD D2DType, const wstring& strSnapMoref,
							  const wstring& JobID, unsigned long blockSize, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd,
							  unsigned long &errorCode, unsigned long &d2dErrorCode,IHADTCallback* pCallback)
{

	PFN_D2D2VmdkSmartCopy pfnD2D2VmdkSmartCopy = NULL;
	// Each function will load a new instance of vmjob.dll.This can avoid concurrent issue in monitor and monitee.
	HMODULE hModule = LoadLibrary(_T("vmjob.dll"));
	DWORD m_ret = 1;

	if(hModule)
	{
		pfnD2D2VmdkSmartCopy = (PFN_D2D2VmdkSmartCopy)GetProcAddress(hModule, "D2D2VmdkSmartCopy");
		if(pfnD2D2VmdkSmartCopy != NULL)
		{
			m_ret = (*pfnD2D2VmdkSmartCopy)(D2DFilePathBegin,D2DFilePathEnd, moref, EsxHostName, EsxUserName, EsxUserPassword,
											VmdkDiskUrl,VDDKPort,moreParams,D2DType,strSnapMoref,JobID,afGUID,blockSize,nBackupDescType,
											NetConnUserName, NetConnPwd, errorCode, d2dErrorCode, pCallback);
		}
	}	
	if(hModule != NULL)
	{
		FreeLibrary(hModule);
		hModule = NULL;
	}
	return m_ret;
		
}

DWORD FacadeUpdateDiskSigViaNBD(const wstring& VHDFileName, const wstring& moref, const wstring& hostName, const wstring& userName, const wstring& password, const wstring& diskUrl, int vddkPort, 
	const VMDK_CONNECT_MORE_PARAMS& moreParams, int diskType, const wstring& snapMoref, const wstring& jobID, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, IHADTCallback* callback)
{

	
	PFN_UpdateDiskSigViaNBD pfnUpdateDiskSigViaNBD = NULL;
	// Each function will load a new instance of vmjob.dll.This can avoid concurrent issue in monitor and monitee.
	HMODULE hModule = LoadLibrary(_T("vmjob.dll"));
	DWORD m_ret = 1;

	if(hModule)
	{
		pfnUpdateDiskSigViaNBD = (PFN_UpdateDiskSigViaNBD)GetProcAddress(hModule, "UpdateDiskSignNbd");
		if(pfnUpdateDiskSigViaNBD != NULL)
		{
			m_ret = (*pfnUpdateDiskSigViaNBD)(VHDFileName, moref, hostName, userName, password, diskUrl, vddkPort, moreParams, diskType, snapMoref, jobID, L"", nBackupDescType, NetConnUserName, NetConnPwd, callback);
		}
	}	
	if(hModule != NULL)
	{
		FreeLibrary(hModule);
		hModule = NULL;
	}
	return m_ret;
		
	
}

//modified by zhepa02 at 2015-04-09 to support vddk 6
//add the thumbPrint parameter
DWORD FacadeGetVMDKSignature(const wstring& strAfguid, 
							const wstring& strEsx, 
							const wstring& strUser, 
							const wstring& strPassword,
							const wstring& strMorefId, 
							UINT32 iPort,
							const VMDK_CONNECT_MORE_PARAMS& moreParams,
							const wstring& strSnapMoref,
							const wstring& strVMDKURL,
							wstring& strDskGUID,
							const wstring& strJobID)
{

	//while (true)
	//{
	//	Sleep(3000);
	//}
	PFN_GetSignature pfnGetSignature = NULL;
	DWORD dret = 1;

	HMODULE hMountVMMgr = NULL;
	hMountVMMgr = LoadLibrary( L"vmJob.dll" );
	if( hMountVMMgr == NULL )
	{
		return 1;
	}

	pfnGetSignature = (PFN_GetSignature)GetProcAddress(hMountVMMgr, "GetVMDKSignature");
	//modified by zhepa02 at 2015-04-09 to support vddk 6,add the thumbPrint parameter
	dret = pfnGetSignature(strEsx, strUser, strPassword, strMorefId, iPort, moreParams, strSnapMoref, strVMDKURL, strDskGUID, strJobID, strAfguid);
	if(hMountVMMgr != NULL)
	{
		FreeLibrary(hMountVMMgr);
		hMountVMMgr = NULL;
	}
	return dret;

}

//modified by zhepa02 at 2015-04-14, add the thumbprint parameter
DWORD FacadeSetVMDKGeometry(const wstring& strEsx, 
							 const wstring& strUser, 
							 const wstring& strPassword, 
							 const wstring& strMorefId, 
							 UINT32 iPort,
							 const VMDK_CONNECT_MORE_PARAMS& moreParams,
							 const wstring& strVMDKURL, 
							 long volOffset,
							 const wstring& strJobID,
							 const wstring& strAfguid)
{

	PFN_SetVMDKGeometry pfnSetGeometry = NULL;
	DWORD dret = 1;

	HMODULE hMountVMMgr = NULL;
	hMountVMMgr = LoadLibrary( L"vmJob.dll" );
	if( hMountVMMgr == NULL )
	{
		return 1;
	}

	pfnSetGeometry = (PFN_SetVMDKGeometry)GetProcAddress(hMountVMMgr, "SetVMDKGeometry");

	//modified by zhepa02 at 2015-04-14, add the thumbprint parameter
	dret = pfnSetGeometry(strEsx, strUser, strPassword, strMorefId, iPort, moreParams, strVMDKURL, volOffset, strJobID, strAfguid);

	if(hMountVMMgr != NULL)
	{
		FreeLibrary(hMountVMMgr);
		hMountVMMgr = NULL;
	}
	return dret;

}

//modified by zhepa02 at 2015-04-14, add the thumbprint parameter
DWORD  DoVMWareDriverInjection(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, 
									 const wstring& strMorefId, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, vector<wstring> strVMDKURL,
									 const VMwareVolumeInfo& volInfo, const wstring& strNewName, const wstring& strFailoverMode,
									 const wstring& key,const wstring& value, const wstring& strJobID, const wstring& strAfguid, bool isUEFI, const wstring& wstrDrzFilePath)
{

	DWORD dResult = 0;
	DWORD iErrorVal = 0;
	PFN_DriverInject pfnDriverInject = NULL;

	HMODULE hMountVMMgr = NULL; 
	hMountVMMgr = LoadLibrary( L"vmJob.dll" );
	if( hMountVMMgr == NULL )
	{
		return 1;
	}

	wstring strSymbolicLnk;
	pfnDriverInject = (PFN_DriverInject)GetProcAddress(hMountVMMgr, "DriverInject");
	if(pfnDriverInject == NULL)
	{
		iErrorVal = 1;	//
		goto clean;
	}
	//modified by zhepa02 at 2015-04-14, add the thumbprint parameter
	dResult = pfnDriverInject(strEsx, strUser, strPassword, strMorefId, volInfo, strNewName, strFailoverMode, iPort, moreParams, strVMDKURL, key, value, strJobID, strAfguid, isUEFI, wstrDrzFilePath);
	
	
clean:	
	if (hMountVMMgr != NULL)
	{	
		FreeLibrary(hMountVMMgr);
		//hMountVMMgr = NULL;
	}
	return dResult;

}

static BOOL SaveVolumeDriversToFile(const vector<std::pair<std::wstring, std::wstring>>& volume_drivers,
	const std::wstring& fileName)
{
	std::wofstream f(fileName.c_str(), std::ios::out | std::ios::trunc);
	if (!f.is_open())
		return FALSE;

	for(std::vector<std::pair<std::wstring, std::wstring>>::const_iterator i = volume_drivers.begin();
		i != volume_drivers.end(); ++i)
	{
		f << i->first.c_str() << i->second.c_str() << L"\r\n";
	}
	f.close();
	return TRUE;
}


//<sonmi01>2012-8-23 #new struct
//modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.
DWORD DoRVCMInjectService(const wstring& strEsx, 
							const wstring& strUser, 
							const wstring& strPassword, 
							const wstring& strMorefId, 
							const VMwareVolumeInfo& volInfo, 
							const wstring& szFailoverMode, 
							UINT32 iPort, 
							const VMDK_CONNECT_MORE_PARAMS& moreParams,
							std::vector<std::wstring> strVMDKURL, 
							const wstring& szFileDirInKey, //reuse key field as file path
							const wstring& JOBID,
							const vector<std::pair<std::wstring, std::wstring>>& volume_drivers,
							bool isX86,
							const wstring& scriptPath	
						)
{
	DWORD iErrorVal = 0;
	PFN_RVCMInjectService pfn_RVCMInjectService = NULL;
	std::wstring rootFolder = volInfo.rootFolder;
	std::wstring wstrBootMngFolder = rootFolder + L"\\IVMBootMgr\\";
	CString cstrBootManagerFolder(wstrBootMngFolder.c_str());
	CString cstrDNSUpdater = cstrBootManagerFolder + TEXT("DNSTOOLS\\update_dns.exe");

	//<sonmi01>2013-1-5 #163426: RVCM - D2D service on converter crashed during conversion job 
	HANativeLogMsg(TEXT("VMDK list size = %d"), strVMDKURL.size());
	for (size_t ii = 0; ii < strVMDKURL.size(); ++ii )
	{
		HANativeLogMsg(TEXT("VMDK = %s"), strVMDKURL[ii].c_str());
	}

	if (0 == strVMDKURL.size())
	{
		return E_INVALIDARG;
	}

	HMODULE hMountVMMgr = NULL; 
	hMountVMMgr = LoadLibrary( L"vmJob.dll" );
	if( hMountVMMgr == NULL )
	{
		return 1;
	}

	wstring DestDir;
	HRESULT hr = PrepareServiceAndToolFiles(hMountVMMgr, DestDir, isX86, scriptPath);
	HANativeLogMsg(TEXT("PrepareServiceAndToolFiles hr=%u, destDir=%s"), hr, DestDir.c_str());

// 	{ //<sonmi01>2012-8-23 ###???
// 		vector<NSRVCM::adapter_map_info> vecAMI;
// 		AdptorInfoVectorW2A(net_adapters, vecAMI);
// 
// 		//p2v_controller conn((unsigned int)0,0,NULL,NULL);
// 		std::wstring p2v_config_file_name = DestDir;
// 		if (USE_WS_P2V)
// 			p2v_config_file_name += L"WS_P2V\\p2v_config.dat"; //<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
// 		else
// 			p2v_config_file_name += L"InstantVmHelper\\p2v_config.dat";
// 		//bool bSave = conn.save_network_mapping_to_file(p2v_config_file_name, vecAMI);
// 		bool bSave = NSRVCM::save_network_mapping_to_file(NSRVCM::platform_esx_t, vecAMI, p2v_config_file_name.c_str());
// 		HANativeLogMsg(TEXT("save_network_mapping_to_file bSave=%u, destDir=%s"), bSave, p2v_config_file_name.c_str());
// 		std::wstring volumeDriversFileName;
// 		if (USE_WS_P2V)
// 			volumeDriversFileName = DestDir + L"WS_P2V\\volume_driver.map";
// 		else
// 			volumeDriversFileName = DestDir + L"InstantVmHelper\\volume_driver.map";
// 		bSave = SaveVolumeDriversToFile(volume_drivers, volumeDriversFileName);
// 		HANativeLogMsg(TEXT("Save volume drivers to file. bSave = %u, fileName = %s"), bSave, volumeDriversFileName.c_str());
// 	}

// 	{
// 		std::wstring wstrBootMngFolder = szVol + L"\\CABootManager\\";
// 		CString cstrBootManagerFolder(wstrBootMngFolder.c_str());
// 
// 		//<sonmi01>2012-8-24 #save service.ini
// 		std::wstring ServiceIniFile = DestDir;
// 		ServiceIniFile += L"CAStartAppService.ini";
// 		TINIFile IniBin;
// 		IniBin.SectionSettings.INIServiceName = TEXT("CAARCserveBootManager");
// 		IniBin.SectionSettings.INIChangeToStartType = TEXT("manual");
// 		IniBin.SectionSettings.INIEndProcessOnServiceStop = TEXT("no");

		//if (dns_redirection.size())
		//{
		//	INISectionProcess InstallVCRT; //<sonmi01>2013-2-1 #win_nsupdate.exe requires vc8.0.50727.762, a very old version VC runtime from vs2005
		//	InstallVCRT.INICommandLine = TEXT("c:\\windows\\CABootManager\\WS_P2V\\vcredist_x86.exe /Q"); 
		//	InstallVCRT.INICurrentDirectory = TEXT("c:\\windows\\CABootManager\\WS_P2V\\"); 
		//	IniBin.SectionProcessItems.push_back(InstallVCRT);
		//}

// 		INISectionProcess ProcessBin;
// 		CString cstrP2VCmdLine;
// 		if (USE_WS_P2V)
// 			cstrP2VCmdLine = cstrBootManagerFolder + TEXT("WS_P2V\\ws_p2v.exe path ") + cstrBootManagerFolder + TEXT("WS_P2V\\p2v_config.dat");
// 		else
// 			cstrP2VCmdLine = cstrBootManagerFolder + TEXT("InstantVmHelper\\ws_p2v.exe ") + L"/Run";
// 		ProcessBin.INICommandLine = cstrP2VCmdLine; //<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
// 
// 		if (USE_WS_P2V)
// 			ProcessBin.INICurrentDirectory = cstrBootManagerFolder + TEXT("WS_P2V\\"); //<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
// 		else
// 			ProcessBin.INICurrentDirectory = cstrBootManagerFolder + TEXT("InstantVmHelper\\");
// 		IniBin.SectionProcessItems.push_back(ProcessBin);

// 		if (dns_redirection.size()) //<sonmi01>2012-9-20 ###???
// 		{
// 			BOOL bencrypted = TRUE;
// 			std::wstring update_dnsIniFile = DestDir;
// 			update_dnsIniFile += L"UpdateDNSTool.ini";
// 
// 			DNSUpdaterSaveBinToIni(
// 				cstrDNSUpdater, 
// 				dns_redirection, 
// 				update_dnsIniFile.c_str(),  
// 				bencrypted);
// 
// 			INISectionProcess ProcessBinDNSTool;
// 			CString cstrBinDNSToolCmdLine = cstrBootManagerFolder + TEXT("UpdateDNSTool.exe ") + cstrBootManagerFolder + TEXT("UpdateDNSTool.ini e");
// 			ProcessBinDNSTool.INICommandLine = cstrBinDNSToolCmdLine;
// 			ProcessBinDNSTool.INICurrentDirectory = cstrBootManagerFolder;
// 			IniBin.SectionProcessItems.push_back(ProcessBinDNSTool);
// 		}

// 		WriteIniFile(ServiceIniFile.c_str(), IniBin);
// 	}
	

	pfn_RVCMInjectService = (PFN_RVCMInjectService)GetProcAddress(hMountVMMgr, "RVCMInjectService");
	if(pfn_RVCMInjectService == NULL)
	{
		iErrorVal = 1;	//
		goto clean;
	}
	iErrorVal = pfn_RVCMInjectService(
		strEsx, 
		strUser, 
		strPassword, 
		strMorefId, 
		volInfo, 
		szFailoverMode, 
		iPort, 
		moreParams,
		strVMDKURL, 
		DestDir, //reuse key field as file path
		JOBID
		);


clean:	
	if (hMountVMMgr != NULL)
	{	
		FreeLibrary(hMountVMMgr);
		hMountVMMgr = NULL;
	}
    static CONST INT MIN_PATH_LENGTH = 6; //<huvfe01>2013-1-17
    if (DestDir.size() > MIN_PATH_LENGTH)
    {
        DeleteDirectoryHelper(DestDir.c_str());
    }
//	iErrorVal = 0;
	return iErrorVal;
}

//modified by zhepa02 at 2015-04-14 , add the thumbprint parameter
DWORD  DriverInjectSingleVMDK(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, const wstring& newHostName, const wstring& szFailoverMode, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strVMDKURL, const wstring& key, const wstring& value, const wstring& JOBID, const wstring& strAfguid)
{

	DWORD dResult = 0;
	DWORD iErrorVal = 0;
	PFN_DriverInjectSingleVMDK pfnDriverInject = NULL;

	HMODULE hMountVMMgr = NULL; 
	hMountVMMgr = LoadLibrary( L"vmJob.dll" );
	if( hMountVMMgr == NULL )
	{
		return 1;
	}

	pfnDriverInject = (PFN_DriverInjectSingleVMDK)GetProcAddress(hMountVMMgr, "DriverInjectSingleVMDK");
	if(pfnDriverInject == NULL)
	{
		iErrorVal = 1;	//
		goto clean;
	}
	//modified by zhepa02 at 2015-04-14, add the thumbprint
	dResult = pfnDriverInject(strEsx, strUser, strPassword, strMorefId, newHostName, szFailoverMode, iPort, moreParams, strVMDKURL, key, value, JOBID, strAfguid);


clean:	
	if (hMountVMMgr != NULL)
	{	
		FreeLibrary(hMountVMMgr);
		//hMountVMMgr = NULL;
	}
	return dResult;

}

BOOL WINAPI FacadeGetGuestID(wstring& strGuestID)
{

	HMODULE hMountHACommondll = NULL; 

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if( hMountHACommondll == NULL )
	{
		return 1;
	}

	PFN_GetGuestID pfnGetGuestID = NULL;
	pfnGetGuestID = (PFN_GetGuestID)GetProcAddress(hMountHACommondll, "GetGuestID");
	BOOL bResult = (*pfnGetGuestID)(strGuestID);
	if (hMountHACommondll != NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}
	return bResult;

}

DWORD WINAPI FacadeInstallVMwareTools()
{
	HaUtility::ComEnvironment::Instance().Start();
	DWORD dResult = 0;
	HMODULE hMountHACommondll = NULL; 

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if( hMountHACommondll == NULL )
	{
		return 1;
	}

	PFN_InstallVMwareTools pfnInstallVMwareTools = NULL;

	pfnInstallVMwareTools = (PFN_InstallVMwareTools)GetProcAddress(hMountHACommondll, "InstallVMwareTools");

	dResult = (*pfnInstallVMwareTools)();

	if (hMountHACommondll != NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

	HaUtility::ComEnvironment::Instance().Shutdown();
	return dResult;

}
DWORD WINAPI FacadeGetVDDKVersion(DWORD& dwVerHigh, DWORD& dwVerLow, DWORD& dwVerSub)
{

	HMODULE hMountHACommondll = NULL; 

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if( hMountHACommondll == NULL )
	{
		return 1;
	}

	PFN_GetVddkVersion pfnGetVddkVersion = NULL;
	pfnGetVddkVersion=(PFN_GetVddkVersion)GetProcAddress(hMountHACommondll,"GetVddkVersion");
	if(pfnGetVddkVersion==NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll=NULL;
		return 1;
	}

	DWORD dwResult = (*pfnGetVddkVersion)(dwVerHigh,dwVerLow,dwVerSub);
	if (hMountHACommondll != NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}
	return dwResult;

}

BOOL CancelConversionVMware(const wstring& JobID)
{
	try{
		PFN_D2DCancelConversion pfnD2D2CancelConversion = NULL;
		HMODULE hModule = LoadLibrary(_T("vmjob.dll"));
		bool m_ret = false;
		if(hModule)
		{
			pfnD2D2CancelConversion = (PFN_D2DCancelConversion)GetProcAddress(hModule, "D2DCancelConversion");
			if(pfnD2D2CancelConversion != NULL)
			{
				m_ret = (*pfnD2D2CancelConversion)(JobID);
			}
			if(m_ret)
			{
				_tprintf(_T("CancelConversionVMware is Successful.\n"));
			}
			else
			{
				_tprintf(_T("CancelConversionVMware is incomplete or failed.\n"));
			}
		}

		if(hModule != NULL)
		{
			FreeLibrary(hModule);
			hModule = NULL;
		}
		return (BOOL)m_ret;
	}	
	catch(...)
	{
		return 1;
	}
}

DWORD WINAPI FacadeHA_LockD2DSessions(/*IN*/const wchar_t* pwszSessRoot, /*IN*/int nSessBegin, /*IN*/int nSessEnd, /*OUT*/HANDLE* phLock, /*IN*/BOOL bRemoteVCM)
{

	HMODULE hMountHACommondll = NULL; 

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if( hMountHACommondll == NULL )
	{
		return 1;
	}

	PFN_HA_LockD2DSessions pfnLockD2DSession = NULL;
	pfnLockD2DSession=(PFN_HA_LockD2DSessions)GetProcAddress(hMountHACommondll,"HA_LockD2DSessions");
	if(pfnLockD2DSession==NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll=NULL;
		return 1;
	}

	DWORD dwResult = (*pfnLockD2DSession)(pwszSessRoot,nSessBegin,nSessEnd, phLock, bRemoteVCM);
	if (hMountHACommondll != NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}
	return dwResult;

}

DWORD WINAPI FacadeHA_UnlockD2DSessions(HANDLE hLock)
{
	HMODULE hMountHACommondll = NULL; 

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if( hMountHACommondll == NULL )
	{
		return 1;
	}

	PFN_HA_UnlockD2DSessions pfnUnlockD2DSession = NULL;
	pfnUnlockD2DSession=(PFN_HA_UnlockD2DSessions)GetProcAddress(hMountHACommondll,"HA_UnlockD2DSessions");
	if(pfnUnlockD2DSession==NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll=NULL;
		return 1;
	}

	pfnUnlockD2DSession(hLock);

	if (hMountHACommondll != NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

	return 0;
}

DWORD WINAPI FacadeHA_UnlockCTF2(HANDLE hLock)
{
	HMODULE hMountHACommondll = NULL; 

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if( hMountHACommondll == NULL )
	{
		return 1;
	}

	PFN_HA_UnlockCTF2 pfnHAUnlockCTF2 = NULL;
	pfnHAUnlockCTF2=(PFN_HA_UnlockCTF2)GetProcAddress(hMountHACommondll,"HA_UnlockCTF2");
	if(pfnHAUnlockCTF2==NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll=NULL;
		return 1;
	}

	pfnHAUnlockCTF2(hLock);

	if (hMountHACommondll != NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

	return 0;
}


//////////////////////////////////////////////////////////////////////////
//BEGIN remote VCM inject service
static VOID __GetFileNameHelper(const wstring & strFullPathFile, wstring & strFileName)
{
	size_t Pos = strFullPathFile.find_last_of(L'\\');
	if (wstring::npos != Pos)
	{
		strFileName = strFullPathFile.substr(Pos + 1);
	}
}

static HRESULT __CopyFilesToDirHelper(const vector<wstring> & SrcFiles, //full path file names
									  const wstring & DestDir) //dest dir ending with '\'
{
	HRESULT hr = S_OK;
	DWORD LastError = 0;

	BOOL bRet = FALSE;

	for (size_t ii = 0; ii < SrcFiles.size(); ++ ii)
	{
		wstring strFileName;
		__GetFileNameHelper(SrcFiles[ii], strFileName);

		wstring strDestFile = DestDir + strFileName;

		bRet = CopyFile(SrcFiles[ii].c_str(), strDestFile.c_str(), FALSE);
		if (!bRet)
		{
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			HANativeLogMsg(L"Failed copy file %s ==> %s with hr = 0x%08x", SrcFiles[ii].c_str(), strDestFile.c_str(), hr);
		}
		else
		{
			HANativeLogMsg(L"Copied file %s ==> %s", SrcFiles[ii].c_str(), strDestFile.c_str());
		}
	}


	return hr;
}

static GUID __CreateGuid()
{
	GUID guid = {0};
	CoCreateGuid(&guid);
	return guid;
}

static wstring __GuidToString(const GUID& guid)
{
	wchar_t buff[64] = {0};
	_snwprintf_s(buff, sizeof buff/sizeof buff[0], 
		L"{%08X-%04X-%04X-%02X%02X-%02X%02X%02X%02X%02X%02X}", guid.Data1, guid.Data2, 
		guid.Data3, guid.Data4[0], guid.Data4[1], guid.Data4[2], guid.Data4[3], guid.Data4[4], 
		guid.Data4[5], guid.Data4[6], guid.Data4[7]);

	return wstring(buff);
}

static HRESULT PrepareServiceAndToolFiles(HMODULE hMountVMMgr, wstring & DestDir, bool isX86, const wstring& scriptPath)
{
	HRESULT	hr = S_OK;
	DWORD	LastError = 0;

	BEGIN_BLOCK(0);
	//////////////////////////////////////////////////////////////////////////
	CONST INT BUFFER_LENGTH = 1024;
	CString strSourcePath;
	LPTSTR pBuffer = strSourcePath.GetBuffer(BUFFER_LENGTH);
	INT CharCount = GetModuleFileName(
		hMountVMMgr,		//_In_opt_  HMODULE hModule,
		pBuffer,			//_Out_     LPTSTR lpFilename,
		BUFFER_LENGTH		//_In_      DWORD nSize
		);
	if (CharCount)
	{
		LPTSTR pTemp = _tcsrchr(pBuffer, TEXT('\\'));
		if (pTemp)
		{
			pTemp[1] = 0;
		}
	}
	strSourcePath.ReleaseBuffer();
	if (0 == CharCount)
	{
		LastError = GetLastError();
		hr = HRESULT_FROM_WIN32(LastError);
		HANativeLogMsg(TEXT("GetModuleFileName failed with hr=0x%08x"), hr);
		break;
	}

	//////////////////////////////////////////////////////////////////////////
	CString strDestPath = strSourcePath;
	strDestPath += __GuidToString(__CreateGuid()).c_str();
	strDestPath += TEXT("\\");
	BOOL bRet = CreateDirectory(
		strDestPath.GetString(),//_In_      LPCTSTR lpPathName,
		NULL//_In_opt_  LPSECURITY_ATTRIBUTES lpSecurityAttributes
		);
	if (!bRet)
	{
		LastError = GetLastError();
		hr = HRESULT_FROM_WIN32(LastError);
		HANativeLogMsg(TEXT("GetModuleFileName failed with hr=0x%08x"), hr);
		break;
	}
	DestDir = strDestPath.GetString();

	//////////////////////////////////////////////////////////////////////////
// 	static CONST LPCTSTR SouceFileList[] = 
// 	{
// 		TEXT("CAStartAppService.exe"),
		//TEXT("ws_p2v.exe"), //<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
		//TEXT("thl.dll"),
		//TEXT("trc.dll"),
		//TEXT("txf_wrapper.dll"),
		//TEXT("msvcr100.dll"),
		//TEXT("msvcp100.dll"),
// 		TEXT("UpdateDNSTool.exe") //<sonmi01>2012-9-20 ###???
		//TEXT("CAStartAppService.ini"),
		//TEXT("p2v_config.dat")
// 	};
// 	vector<wstring> vecSourceFiles;
// 	for (int ii = 0; ii < _countof(SouceFileList); ii++)
// 	{
// 		wstring strFiletemp = strSourcePath.GetString();
// 		strFiletemp += SouceFileList[ii];
// 		vecSourceFiles.push_back(strFiletemp);
// 	}


	//////////////////////////////////////////////////////////////////////////
// 	hr = __CopyFilesToDirHelper(vecSourceFiles, strDestPath.GetString());
// 	if (FAILED(hr))
// 	{
// 		HANativeLogMsg(TEXT("__CopyFilesToDirHelper failed with hr=0x%08x"), hr);
// 		//break;
// 	}

	//////////////////////////////////////////////////////////////////////////
	//<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
	CString strWS_P2VSourceDir = strSourcePath;
	strWS_P2VSourceDir.TrimRight(TEXT("\\/"));
	strWS_P2VSourceDir = isX86 ? strWS_P2VSourceDir + TEXT("\\InstantVmHelper\\X86") : strWS_P2VSourceDir + TEXT("\\InstantVmHelper\\AMD64");

	CString strWS_P2VDir = strDestPath;
	strWS_P2VDir.TrimRight(TEXT("\\/"));

	CreateDirectory(strWS_P2VDir.GetString(), NULL);
	hr = CopyFileTree(strWS_P2VSourceDir.GetString(), strWS_P2VDir.GetString(), TEXT("*"));
	if (FAILED(hr))
	{
		HANativeLogMsg(TEXT("CopyFileTree failed [%s] -- > [%s] with hr=0x%08x"), strWS_P2VSourceDir.GetString(), strWS_P2VDir.GetString(), hr);
		//break;
	}

	// Copy instant vm helper script
	strWS_P2VDir = strWS_P2VDir + L"\\InstantVMHelperScript.xml";
	CopyFile(scriptPath.c_str(), strWS_P2VDir.GetString(), FALSE);
	DeleteFile(scriptPath.c_str());

	//<sonmi01>2012-9-20 ###???
	CString strDNSSourceDir = strSourcePath;
	strDNSSourceDir.TrimRight(TEXT("\\/"));
	strDNSSourceDir += TEXT("\\DNSTOOLS");

	CString strDNSDir = strDestPath;
	strDNSDir.TrimRight(TEXT("\\/"));
	strDNSDir += TEXT("\\DNSTOOLS");
	CreateDirectory(strDNSDir.GetString(), NULL);
	hr = CopyFileTree(strDNSSourceDir.GetString(), strDNSDir.GetString(), TEXT("*"));
	if (FAILED(hr))
	{
		HANativeLogMsg(TEXT("CopyFileTree failed [%s] -- > [%s] with hr=0x%08x"), strDNSSourceDir.GetString(), strDNSDir.GetString(), hr);
		//break;
	}

	END_BLOCK(0);

	return hr;
}
//END remote VCM inject service
//////////////////////////////////////////////////////////////////////////


//<sonmi01>2012-8-23 ###???
static VOID AdptorInfoW2A(CONST Adapter_Map_InfoW & wInfo, NSRVCM::adapter_map_info & aInfo)
{
	CStringA strA;

	//bool				replicate_mac;
	aInfo.replicate_mac = wInfo.replicate_mac;

	//std::wstring			device_id;
	strA = wInfo.device_id.c_str();
	aInfo.device_id = strA.GetString();


	//std::wstring			mac_address;
	strA = wInfo.mac_address.c_str();
	aInfo.mac_address = strA.GetString();

	//bool				use_dhcp;
	aInfo.ip_use_dhcp = wInfo.ip_use_dhcp;
	aInfo.dns_use_dhcp = wInfo.dns_use_dhcp;

	//bool				use_original_setting;
	aInfo.use_original_setting = wInfo.use_original_setting;

	//std::wstring			wins_p;
	strA = wInfo.wins_p.c_str();
	aInfo.wins_p = strA.GetString();

	//std::wstring			wins_s;
	strA = wInfo.wins_s.c_str();
	aInfo.wins_s = strA.GetString();

	//std::vector<std::wstring> ips;
	aInfo.ips.resize(wInfo.ips.size());
	for (size_t ii = 0; ii < wInfo.ips.size(); ++ ii)
	{
		strA = wInfo.ips[ii].c_str();
		aInfo.ips[ii] = strA.GetString();
	}


	//std::vector<std::wstring> subnets;
	aInfo.subnets.resize(wInfo.subnets.size());
	for (size_t ii = 0; ii < wInfo.subnets.size(); ++ ii)
	{
		strA = wInfo.subnets[ii].c_str();
		aInfo.subnets[ii] = strA.GetString();
	}


	//std::vector<std::wstring> gateways;
	aInfo.gateways.resize(wInfo.gateways.size());
	for (size_t ii = 0; ii < wInfo.gateways.size(); ++ ii)
	{
		strA = wInfo.gateways[ii].c_str();
		aInfo.gateways[ii] = strA.GetString();
	}


	//std::vector<std::wstring> dns;
	aInfo.dns.resize(wInfo.dns.size());
	for (size_t ii = 0; ii < wInfo.dns.size(); ++ ii)
	{
		strA = wInfo.dns[ii].c_str();
		aInfo.dns[ii] = strA.GetString();
	}
}


static VOID AdptorInfoVectorW2A(CONST vector<Adapter_Map_InfoW> & wInfo, vector<NSRVCM::adapter_map_info> & aInfo)
{
	aInfo.resize(wInfo.size());
	for (size_t ii = 0; ii < wInfo.size(); ++ ii)
	{
		AdptorInfoW2A(wInfo[ii], aInfo[ii]);
	}
}

static VOID __GetImagePathHelper(LPCTSTR pDir, wstring & ImagePath)
{
	ImagePath = pDir;
	ImagePath += L"\\CABootManager\\CAStartAppService.exe";
}

static VOID __GetDependOnServiceHelper(vector<wstring> & DependOnService)
{
	DependOnService.push_back(TEXT("LanmanServer"));
	DependOnService.push_back(TEXT("Winmgmt"));
	DependOnService.push_back(TEXT("PlugPlay"));
	DependOnService.push_back(TEXT("Netman"));
}


//vector<wstring> SrcFiles;
//wstring ImagePath;
//vector<wstring> DependOnService; //<sonmi01>2012-8-17 #inject service #001 to do xxxxxx

//LPCTSTR pDir with ending '\'
static VOID __FindAllFilesHelper(LPCTSTR pDir, LPCTSTR pFilePattern, vector<wstring> & VecFiles)
{
	wstring strFilePattern = pDir;
	strFilePattern += pFilePattern;

	WIN32_FIND_DATA FindFileData = {0};
	HANDLE hFind =  FindFirstFile(
		strFilePattern.c_str(), //_In_   LPCTSTR lpFileName,
		&FindFileData//_Out_  LPWIN32_FIND_DATA lpFindFileData
		);
	if (INVALID_HANDLE_VALUE == hFind)
	{
		return;
	}

	do 
	{
		if (!(FILE_ATTRIBUTE_DIRECTORY & FindFileData.dwFileAttributes))
		{
			wstring strFile = pDir;
			strFile += FindFileData.cFileName;
			VecFiles.push_back(strFile);
		}

	} while (FindNextFile(hFind, &FindFileData));

	FindClose(hFind);
	hFind = INVALID_HANDLE_VALUE;

}

//<sonmi01>2012-8-28 ###???
DWORD DoRVCMInjectServiceForHyperV(
									const wstring& strVMWinDir, 
									const wstring& strWinSystemDir, 
									const vector<std::pair<std::wstring, std::wstring>>& volume_drivers,
									bool isX86,
									const wstring& wstrScriptPath
								    )
{
	DWORD iErrorVal = 0;
	
	typedef DWORD(WINAPI HACOMMFUNC_RVCM_InjectHelperServiceToHiveForVsb)(const vector<wstring>& SrcFiles, const wstring& strSystemDir,
		LPCTSTR ImagePath,
		const vector<wstring> & DependOnService,
		LPCTSTR ServiceName,
		DWORD WOW64,
		LPCTSTR DisplayName,
		LPCTSTR Description,
		DWORD ErrorControl /*= SERVICE_ERROR_NORMAL*/,
		LPCTSTR ObjectName /*= L"LocalSystem"*/,
		DWORD Start /*= SERVICE_AUTO_START*/,
		DWORD Type /*= SERVICE_WIN32_OWN_PROCESS*/,
		void* Hive);

	static CONST TCHAR DllName[] = TEXT("HaCommonFunc.dll");
	static CONST CHAR FuncName[] = "Ivm_InjectHelperServiceToHiveForVsb";
	HACOMMFUNC_RVCM_InjectHelperServiceToHiveForVsb * pRVCM_InjectHelperServiceToHiveForVsb = NULL;

	std::wstring strDisplayName(MAX_PATH, L'\0');
	std::wstring strDescription(MAX_PATH * 2, L'\0');

	CString VMWinDir = strVMWinDir.c_str();
	VMWinDir.TrimRight(TEXT("\\/"));
	VMWinDir += TEXT("\\");

	wstring ImagePath;
	vector<wstring> DependOnService;
	vector<wstring> SrcFiles;
	wstring DestDir;
	CString VMWinDirNoBackSlash;
	std::wstring wstrBootMngFolder = strWinSystemDir + L"\\IVMBootMgr\\";
	CString cstrBootManagerFolder(wstrBootMngFolder.c_str());

	HMODULE hCommDll = NULL; 
	hCommDll = LoadLibrary(DllName);
	if( hCommDll == NULL )
	{
		return -10;
	}

	pRVCM_InjectHelperServiceToHiveForVsb = (HACOMMFUNC_RVCM_InjectHelperServiceToHiveForVsb *)GetProcAddress(hCommDll, FuncName);
	if (NULL == pRVCM_InjectHelperServiceToHiveForVsb)
	{
		iErrorVal = -20;
		goto clean;
	}

	
	HRESULT hr = PrepareServiceAndToolFiles(hCommDll, DestDir, isX86, wstrScriptPath);
	HANativeLogMsg(TEXT("PrepareServiceAndToolFiles hr=%u, destDir=%s"), hr, DestDir.c_str());

	// inject instant vm helper service
// 	InjectServiceRegistry(strWinSystemDir, strVMWinDir, hCommDll);

// 	{ //<sonmi01>2012-8-23 ###???
// 		vector<NSRVCM::adapter_map_info> vecAMI;
// 		AdptorInfoVectorW2A(net_adapters, vecAMI);
// 
// 		//p2v_controller conn((unsigned int)0,0,NULL,NULL);
// 		std::wstring p2v_config_file_name = DestDir;
// 		if (USE_WS_P2V)
// 			p2v_config_file_name += L"WS_P2V\\p2v_config.dat"; //<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
// 		else
// 			p2v_config_file_name += L"InstantVmHelper\\p2v_config.dat";
// 		//bool bSave = conn.save_network_mapping_to_file(p2v_config_file_name, vecAMI);
// 		bool bSave = NSRVCM::save_network_mapping_to_file(NSRVCM::platform_hyperv_t, vecAMI, p2v_config_file_name.c_str());
// 		HANativeLogMsg(TEXT("save_network_mapping_to_file bSave=%u, destDir=%s"), bSave, p2v_config_file_name.c_str());
// 		std::wstring volumeDriversFileName;
// 		if (USE_WS_P2V)
// 			volumeDriversFileName = DestDir + L"WS_P2V\\volume_driver.map";
// 		else
// 			volumeDriversFileName = DestDir + L"InstantVmHelper\\volume_driver.map";
// 		bSave = SaveVolumeDriversToFile(volume_drivers, volumeDriversFileName);
// 		HANativeLogMsg(TEXT("Save volume drivers to file. bSave = %u, fileName = %s"), bSave, volumeDriversFileName.c_str());
// 	}

// 	{
		
// 		CString cstrBootManagerFolder(wstrBootMngFolder.c_str());

		//<sonmi01>2012-8-24 #save service.ini
// 		std::wstring ServiceIniFile = DestDir;
// 		ServiceIniFile += L"CAStartAppService.ini";
// 		TINIFile IniBin;
// 		IniBin.SectionSettings.INIServiceName = TEXT("CAARCserveBootManager");
// 		IniBin.SectionSettings.INIChangeToStartType = TEXT("manual");
// 		IniBin.SectionSettings.INIEndProcessOnServiceStop = TEXT("no");

		//if (dns_redirection.size())
		//{
		//	INISectionProcess InstallVCRT; //<sonmi01>2013-2-1 #win_nsupdate.exe requires vc8.0.50727.762, a very old version VC runtime from vs2005
		//	InstallVCRT.INICommandLine = TEXT("c:\\windows\\CABootManager\\WS_P2V\\vcredist_x86.exe /Q"); 
		//	InstallVCRT.INICurrentDirectory = TEXT("c:\\windows\\CABootManager\\WS_P2V\\"); 
		//	IniBin.SectionProcessItems.push_back(InstallVCRT);
		//}

// 		INISectionProcess ProcessBin;
// 		CString cstrP2VCmdLine;
// 		if (USE_WS_P2V)
// 			cstrP2VCmdLine = cstrBootManagerFolder + TEXT("WS_P2V\\ws_p2v.exe path ") + cstrBootManagerFolder + TEXT("WS_P2V\\p2v_config.dat");
// 		else
// 			cstrP2VCmdLine = cstrBootManagerFolder + TEXT("InstantVmHelper\\InstantVmHelper.exe ") + L"/Run";
// 		ProcessBin.INICommandLine = cstrP2VCmdLine; //<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
// 		ProcessBin.INICurrentDirectory = cstrBootManagerFolder + TEXT("WS_P2V\\"); //<sonmi01>2012-11-13 #move ws_p2v into a seperate folder
// // 		ProcessBin.INICurrentDirectory = cstrBootManagerFolder + TEXT("InstantVmHelper\\");
// 		IniBin.SectionProcessItems.push_back(ProcessBin);

// 		if (dns_redirection.size()) //<sonmi01>2012-9-20 ###???
// 		{
// 			BOOL bencrypted = TRUE;
// 			std::wstring update_dnsIniFile = DestDir;
// 			update_dnsIniFile += L"UpdateDNSTool.ini";
// 			CString cstrDNSUpdater = cstrBootManagerFolder + TEXT("DNSTOOLS\\update_dns.exe");
// 			DNSUpdaterSaveBinToIni(
// 				cstrDNSUpdater, 
// 				dns_redirection, 
// 				update_dnsIniFile.c_str(),  
// 				bencrypted);
// 
// 			INISectionProcess ProcessBinDNSTool;
// 			CString cstrBinDNSToolCmdLine = cstrBootManagerFolder + TEXT("UpdateDNSTool.exe ") + cstrBootManagerFolder + TEXT("UpdateDNSTool.ini e");
// 			ProcessBinDNSTool.INICommandLine = cstrBinDNSToolCmdLine;
// 			ProcessBinDNSTool.INICurrentDirectory = cstrBootManagerFolder;
// 			IniBin.SectionProcessItems.push_back(ProcessBinDNSTool);
// 		}

// 		WriteIniFile(ServiceIniFile.c_str(), IniBin);
// 	}

	ImagePath = wstrBootMngFolder + L"InstantVMHelper.exe /Run";
// 	__GetImagePathHelper(strWinSystemDir.c_str(), ImagePath);
	__GetDependOnServiceHelper(DependOnService);
	//__FindAllFilesHelper(DestDir.c_str(), TEXT("*"), SrcFiles);
	SrcFiles.push_back(DestDir);

	UDPFormatMessageW(0, &strDisplayName[0], MAX_PATH, AFRES_COMMON_HELPER_SERVICE_DISPALY_NAME);
	UDPFormatMessageW(0, &strDescription[0], MAX_PATH * 2, AFRES_COMMON_HELPER_SERVICE_DESCRIPTION);

	VMWinDirNoBackSlash = strVMWinDir.c_str();
	VMWinDirNoBackSlash.TrimRight(TEXT("\\/"));
	iErrorVal = pRVCM_InjectHelperServiceToHiveForVsb(
									SrcFiles,
									strVMWinDir,								//const wstring& strSystemDir, 
									ImagePath.c_str(),							//LPCTSTR ImagePath, 
									DependOnService,							//const vector<wstring> & DependOnService, 
									IVM_HELPER_SVC_NAME,							//LPCTSTR ServiceName	/*= L"ivmhelpersvc"*/, 
									1,											//DWORD WOW64			/*= 1*/, 
									strDisplayName.c_str(),					    //LPCTSTR DisplayName	/*= L"Arcserve UDP VM Helper Service"*/, 
									strDescription.c_str(),						//LPCTSTR Description,   Service Description 
									SERVICE_ERROR_NORMAL,						//DWORD ErrorControl	/*= SERVICE_ERROR_NORMAL*/, 
									L"LocalSystem",								//LPCTSTR ObjectName	/*= L"LocalSystem"*/, 
									SERVICE_AUTO_START,							//DWORD Start			/*= SERVICE_AUTO_START*/, 
									SERVICE_WIN32_OWN_PROCESS,					//DWORD Type			/*= SERVICE_WIN32_OWN_PROCESS */
									NULL
		);

clean:	
	if (hCommDll != NULL)
	{	
		FreeLibrary(hCommDll);
		hCommDll = NULL;
	}
    static CONST INT MIN_PATH_LENGTH = 6; //<huvfe01>2013-1-17
    if (DestDir.size() > MIN_PATH_LENGTH)
    {
        DeleteDirectoryHelper(DestDir.c_str());
    }
	return iErrorVal;
}
