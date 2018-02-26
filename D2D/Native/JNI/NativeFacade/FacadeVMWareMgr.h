#pragma once
#include "HAClientProxy.h"
#include "NativeFacade.h" //<sonmi01>2012-8-23 #new struct
#include "VMDKIo.h"
#include "VMwareVolumeInfo.h"


DWORD ConvertVHD2VMDK(const wstring& afGUID, const wstring& VHDFileName, const wstring& moref, const wstring& hostName, const wstring& userName, const wstring& password, const wstring& diskUrl, int vddkPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, int diskType, const wstring& snapMoref, const wstring& jobID, unsigned long blockSize, int nBackupDescType, 
	 const wstring& NetConnUserName, const wstring& NetConnPwd, unsigned long &errorCode, unsigned long &d2dErrorCode, IHADTCallback* callback);

DWORD FacadeD2D2VmdkSmartCopy(const wstring& afGUID, const wstring& D2DFilePathBegin, const wstring& D2DFilePathEnd, const wstring& Moref, const wstring& EsxHostName, const wstring& EsxUserName, const wstring& EsxUserPassword, const wstring& VmdkDiskUrl, DWORD VDDKPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, DWORD D2DType, const wstring& strSnapMoref,
	const wstring& JobID, unsigned long blockSize, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, unsigned long &errorCode, unsigned long &d2dErrorCode, IHADTCallback* pCallback);

DWORD FacadeUpdateDiskSigViaNBD(const wstring& VHDFileName, const wstring& moref, const wstring& hostName, const wstring& userName, const wstring& password, const wstring& diskUrl, int vddkPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, int diskType, const wstring& snapMoref, const wstring& jobID, int nBackupDescType, const wstring& NetConnUserName, const wstring& NetConnPwd, IHADTCallback* callback);

//modified by zhepa02 at 2015-04-13 , add the thumbprint parameter
DWORD FacadeGetVMDKSignature(const wstring& strAfguid, const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strSnapMoref, const wstring& strVMDKURL, wstring& strDskGUID, const wstring& strJobID);
DWORD FacadeSetVMDKGeometry(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strVMDKURL, long volOffset, const wstring& strJobID, const wstring& strAfguid);

DWORD DoVMWareDriverInjection(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, vector<std::wstring> strVMDKURL, const VMwareVolumeInfo& volInfo, const wstring& strNewName, const wstring& strFailoverMode, const wstring& key, const wstring& value, const wstring& strJobID, const wstring& strAfguid, bool isUEFI, const wstring& wstrDrzFilePath);
DWORD DriverInjectSingleVMDK(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, const wstring& newHostName, const wstring& szFailoverMode, UINT32 iPort, const VMDK_CONNECT_MORE_PARAMS& moreParams, const wstring& strVMDKURL, const wstring& key, const wstring& value, const wstring& JOBID, const wstring& strAfguid);
//<sonmi01>2012-8-23 #new struct
DWORD DoRVCMInjectService(const wstring& strEsx, const wstring& strUser, const wstring& strPassword, const wstring& strMorefId, const VMwareVolumeInfo& volInfo, const wstring& szFailoverMode, UINT32 iPort,
	const VMDK_CONNECT_MORE_PARAMS& moreParams, std::vector<std::wstring> strVMDKURL, const wstring& szFileDirInKey, //reuse key field as file path
	const wstring& JOBID, const vector<std::pair<std::wstring, std::wstring>>& volume_drivers, bool isX86, const wstring& scriptPath);
//modify end

BOOL WINAPI FacadeGetGuestID(wstring& strGuestID);

DWORD WINAPI FacadeInstallVMwareTools();

BOOL CancelConversionVMware(const wstring& JobID);

DWORD WINAPI FacadeGetVDDKVersion(DWORD& dwVerHigh, DWORD& dwVerLow, DWORD& dwVerSub);

DWORD WINAPI FacadeHA_LockD2DSessions(/*IN*/const wchar_t* pwszSessRoot, /*IN*/int nSessBegin, /*IN*/int nSessEnd, /*OUT*/HANDLE* phLock, /*IN*/BOOL bRemoteVCM);

DWORD WINAPI FacadeHA_UnlockD2DSessions(HANDLE hLock);

DWORD WINAPI FacadeHA_UnlockCTF2(HANDLE hLock);

DWORD DoRVCMInjectServiceForHyperV(
								   const wstring& strVMWinDir, 
								   const wstring& wstrWinSystemDir,
								   const vector<std::pair<std::wstring, std::wstring>>& volume_drivers,
								   bool isX86,
								   const wstring& wstrScriptPath		
								   );