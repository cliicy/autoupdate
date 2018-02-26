#pragma once
#include "afjob.h"
#include <windows.h>
#include <string>
using namespace std;

struct CatJobScript
{
	void Clear()
    {
        ulVersion = 0;
		ullBackupTime = 0;
		dwJobNum = 0;
		dwSessNum = 0;
		dwCryptoInfo =0;
		dwCompressInfo =0;
		dwOption = 0;
		dwJobMethod = 0;
		dwJobType = 0;
		llCataLogSize = 0;

		wsSessPWD.clear();
		wsVMName.clear();
		wsVMIP.clear();
        wsVMGUID.clear();       
        wsScriptPath.clear();
        wsBKStartTime.clear();
        wsBKJobName.clear();
        wsBKSessGUID.clear();
		wsRPSName.clear();      
	    wsPolicyGUID.clear();
	    wsPolicyName.clear();
	    wsD2DClientNode.clear();
	    wsSessionRootPath.clear();
		wsSourceDataStore.clear();
		wsSourceDataStoreName.clear();
		wsTargetDataStore.clear();
		wsTargetDataStoreName.clear();
		gInfo.RawSize = 0;
		gInfo.CompressedSize = 0;
		gInfo.CompressRatio = 0;
		gInfo.CompressPercentage = 0;
    }
	ULONG		  ulVersion;				// xml version--zouju01-2012/2/10
	ULONGLONG	  ullBackupTime;			// backup time
    DWORD         dwJobNum;
    DWORD         dwSessNum;
    DWORD         dwCryptoInfo;
    DWORD         dwCompressInfo;
    DWORD         dwOption;
    DWORD         dwJobMethod;
    DWORD         dwJobType;
	ULONGLONG	  llCataLogSize;
    wstring       wsSessPWD;
    wstring       wsVMName;
    wstring       wsVMIP;
    wstring       wsVMGUID;       //ZZ: Currently only for vsphere.
    wstring       wsScriptPath;
    wstring       wsBKStartTime;
    wstring       wsBKJobName;
    wstring       wsBKSessGUID;
	wstring		  wsRPSName;      
	wstring       wsPolicyGUID;
	wstring		  wsPolicyName;
	wstring		  wsD2DClientNode;
	wstring		  wsSessionRootPath;
	wstring       wsSourceDataStore;
	wstring       wsSourceDataStoreName;
	wstring       wsTargetDataStore;
	wstring       wsTargetDataStoreName;
	GDDInformation gInfo;
};