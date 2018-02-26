#pragma once

#include "IVolumeBitmap.h"

#ifdef AFBITMAPMANAGER_EXPORTS
	#define AFBITMAPMGRAPI __declspec(dllexport)
#else
	#define AFBITMAPMGRAPI __declspec(dllimport)
#endif

#define FILECOPY_APP_ID		L"FILECP_APP_ID"

typedef struct _VOLUME_EXT_INFO
{
	vector<wstring>		vecVmdkFileName;			//filled for both backup and restore
	vector<LONGLONG>	vecDiskExtentStartingOffset;   //filled for both backup and restore
	vector<LONGLONG>	vecDiskExtentLength;           //filled for both backup and restore
	vector<LONGLONG>	vecVolumeOffset;				//filled for both backup and restore
	D2D_ENCRYPTION_INFO D2DEncryptionInfo;
	DWORD		dwNumOfDiskExtents; 		//filled for both backup and restore
	DWORD		dwVolType; //VOLUME_TYPE enum
	DWORD		dwVolClusterSize;
	LONGLONG    llVolSize;
	int jobNumber;
}VOLUME_EXT_INFO, *LPVOLUME_EXT_INFO;


typedef struct _SESSION_INFORMATION
{
	UINT PreviousSession;
	UINT CurrentSession;
	wstring currSessPath;
	wstring prevSessPath;

}SESSION_INFORMATION, *LPSESSION_INFORMATION;

class IBitmapManager
{
	//********************************************************************
public:

	virtual DWORD GetBitMap(IVolumeBitmap** ppBitmap, BOOL bFullBackup) = 0;
	virtual DWORD SyncBitmap(IVolumeBitmap* pBitmap) = 0; // can call without calling Get Bitmap if am doing full.
	virtual	DWORD InitBitMapManager(const  LPVOLUME_EXT_INFO pVolInfo, const LPSESSION_INFORMATION pSessInfo, const wstring &wsVolumeGuid, const wstring &nAppUniqId) = 0;
	virtual DWORD DeinitBitMapManager() = 0;
};

extern "C" AFBITMAPMGRAPI DWORD CreateBitmapInterface(IBitmapManager **pIntBitmapManager);

extern "C" AFBITMAPMGRAPI VOID ReleaseBitmapInterface(IBitmapManager **pIntBitmapManager);
extern "C" AFBITMAPMGRAPI DWORD StartSessionChangeTracking(WCHAR* pwzNodePath, WCHAR* pwzUserName, WCHAR* pwzPassWord);

extern "C" AFBITMAPMGRAPI DWORD StopSessionChangeTracking(WCHAR* pwzNodePath, WCHAR* pwzUserName, WCHAR* pwzPassWord);

extern "C" AFBITMAPMGRAPI DWORD DeleteSessionChangeTracking(WCHAR* pwzNodePath, WCHAR* pwzUserName, WCHAR* pwzPassWord, UINT nLastSessionNo);

typedef  DWORD  (*FN_STARTSESSIONCHANGETRACKING)   (WCHAR* pwzNodePath, WCHAR* pwzUserName, WCHAR* pwzPassWord);
typedef  DWORD	(*FN_STOPSESSIONCHANGETRACKING)    (WCHAR* pwzNodePath, WCHAR* pwzUserName, WCHAR* pwzPassWord);
typedef  DWORD  (*FN_DELETESESSIONCHANGETRACKING)  (WCHAR* pwzNodePath, WCHAR* pwzUserName, WCHAR* pwzPassWord, UINT nLastSessionNo);

#define ALL_SESSION_DATA 0xffffffff 