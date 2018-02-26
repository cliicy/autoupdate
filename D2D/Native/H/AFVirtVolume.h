#pragma once

#define	AF_VSOURCE_SESSION		0	// Local D2D session
#define	AF_VSOURCE_SNAPSHOT		1	// Not support yet
#define	AF_VSOURCE_JOURNAL		2	// For journal session support
#define	AF_VSOURCE_REMOTE_SESS	3	// Remote session cross Internet

//////////////////////////////////////////////////////////////////////////
// Error code define ??
#define  ERROR_PARAMTER_ZERO  0XFF01


typedef   void* AFVOLHANDLE;

//////////////////////////////////////////////////////////////////////////
// Disk extension information
typedef struct _AFVOLUME_DISK_EXTENT_
{
	ULONG ulDiskSignature;
	DWORD dwMemberIndex;
	LONGLONG llStartOffSet;
	LONGLONG llLength;
	LONGLONG llVolumeOffSet;
}AFVOLUME_DISK_EXTENT, *PAFVOLUME_DISK_EXTENT;

//////////////////////////////////////////////////////////////////////////
//  volume parameter,  variable size
typedef struct _AFOPEN_VVOL_PARAMETERS_
{
	bool  bReadOnly;
	GUID  guidDataStore;
	ULONG ulSourceType;
	ULONG ulRootPathOffSet;
	ULONG ulRootPathLength;
	ULONG ulUserNameOffSet;
	ULONG ulUserNameLength;
	ULONG ulPathPwdOffSet;
	ULONG ulPathPwdLength;
	ULONG ulSessionNumber;
	ULONG ulSessPwdOffSet;
	ULONG ulSessPwdLength;
	ULONG ulDataSaveVolumeOffset;
	ULONG ulDataSaveVolumeLength;
	ULONG ulVolumeType;
	ULONG ulExtentsCount;
	ULONG ulExtentsOffSet;	
}AFOPEN_VVOL_PARAMETERS, *PAFOPEN_VVOL_PARAMETERS;

//////////////////////////////////////////////////////////////////////////
//  server connect info
typedef struct _srv_info
{
	DWORD	dwProxyType;
	USHORT	usPort;
	WCHAR*	szHostName;
	WCHAR*	szUserName;
	WCHAR*	szUserPwd;
}ST_SRVINFO, *PST_SRVINFO;

#ifndef USE_DDK
#include <string>
#include <vector>
using namespace std;
typedef std::vector<AFVOLUME_DISK_EXTENT> AFDISK_EXTS;

//////////////////////////////////////////////////////////////////////////
//  open session information
typedef struct _AFVOLUME_OPEN_
{
	DWORD   dwSourceType;			// source: from session or somewhere.
	wstring strRootPath;			// session full path
	wstring strUserName;			// user: domain\name
	wstring strPassword;			// user: password
	wstring strSaveDataVolume;		// volume path to save temp write data, default C:
	DWORD   dwSessionNumber;		// session number
	wstring strSessionPwd;			// session password
	BOOL    bReadOnly;				// Read Only or Read Write
	GUID	guidDataStore;			// data store GUID
	DWORD   dwVolumeType;			// Volume type: Simple Volume or Strip Volume or Span Volume
	AFDISK_EXTS  DiskExts;			// Volume disk extension
}AFVOLUME_OPEN, *PAFVOLUME_OPEN;

// Convert parameter
inline DWORD GetPlainMemoryParameter(const AFVOLUME_OPEN& VolumeOpen, PAFOPEN_VVOL_PARAMETERS& pOpenParameter, DWORD& dwParameterSize)
{
	DWORD dwStartOffset = max(sizeof(AFVOLUME_OPEN), 512);
	size_t tSize = 0;//dwStartOffset;
	tSize += VolumeOpen.strRootPath.size()+1;

	tSize += VolumeOpen.strPassword.size()+1;
	tSize += VolumeOpen.strUserName.size()+1;
	tSize += VolumeOpen.strSessionPwd.size()+1;
	tSize += VolumeOpen.strSaveDataVolume.size()+1;
	//tSize += VolumeOpen.strAdmin.size()+1;
	//tSize += VolumeOpen.strAdminPwd.size()+1;
	tSize *= sizeof(WCHAR);
	tSize += dwStartOffset;
	tSize += VolumeOpen.DiskExts.size()*sizeof(AFVOLUME_DISK_EXTENT);

	pOpenParameter = (PAFOPEN_VVOL_PARAMETERS)new char[tSize];
	if(pOpenParameter == NULL)
	{
		return ERROR_NOT_ENOUGH_MEMORY;
	}
	ZeroMemory(pOpenParameter, tSize);

	pOpenParameter->guidDataStore = VolumeOpen.guidDataStore;
	pOpenParameter->bReadOnly = VolumeOpen.bReadOnly ? true : false;
	pOpenParameter->ulExtentsCount = (ULONG)VolumeOpen.DiskExts.size();
	pOpenParameter->ulSourceType   = VolumeOpen.dwSourceType;
	pOpenParameter->ulVolumeType   = VolumeOpen.dwVolumeType;
	pOpenParameter->ulSessionNumber= VolumeOpen.dwSessionNumber;

	pOpenParameter->ulRootPathLength = (ULONG)VolumeOpen.strRootPath.size();
	pOpenParameter->ulRootPathOffSet = dwStartOffset;
	LPWSTR lpString = (LPWSTR)((char*)pOpenParameter + dwStartOffset);
	wcscpy_s(lpString, pOpenParameter->ulRootPathLength+1, VolumeOpen.strRootPath.c_str());
	dwStartOffset += (pOpenParameter->ulRootPathLength +1)*sizeof(WCHAR);

	pOpenParameter->ulUserNameLength = (ULONG)VolumeOpen.strUserName.size();
	pOpenParameter->ulUserNameOffSet = dwStartOffset;
	lpString = (LPWSTR)((char*)pOpenParameter + dwStartOffset);
	wcscpy_s(lpString, pOpenParameter->ulUserNameLength+1, VolumeOpen.strUserName.c_str());
	dwStartOffset +=(pOpenParameter->ulUserNameLength +1)*sizeof(WCHAR);

	pOpenParameter->ulPathPwdLength = (ULONG)VolumeOpen.strPassword.size();
	pOpenParameter->ulPathPwdOffSet = dwStartOffset;
	lpString = (LPWSTR)((char*)pOpenParameter + dwStartOffset);
	wcscpy_s(lpString, pOpenParameter->ulPathPwdLength+1, VolumeOpen.strPassword.c_str());
	dwStartOffset +=(pOpenParameter->ulPathPwdLength +1)*sizeof(WCHAR);;

	pOpenParameter->ulSessPwdLength = (ULONG)VolumeOpen.strSessionPwd.size();
	pOpenParameter->ulSessPwdOffSet = dwStartOffset;
	lpString = (LPWSTR)((char*)pOpenParameter + dwStartOffset);
	wcscpy_s(lpString, pOpenParameter->ulSessPwdLength+1, VolumeOpen.strSessionPwd.c_str());
	dwStartOffset +=(pOpenParameter->ulSessPwdLength +1)*sizeof(WCHAR);;

	pOpenParameter->ulDataSaveVolumeLength = (ULONG)VolumeOpen.strSaveDataVolume.size();
	pOpenParameter->ulDataSaveVolumeOffset = dwStartOffset;
	lpString = (LPWSTR)((char*)pOpenParameter + dwStartOffset);
	wcscpy_s(lpString, pOpenParameter->ulDataSaveVolumeLength+1, VolumeOpen.strSaveDataVolume.c_str());
	dwStartOffset +=(pOpenParameter->ulDataSaveVolumeLength+1)*sizeof(WCHAR);;

	/*pOpenParameter->ulAdminLength = (ULONG)VolumeOpen.strAdmin.size();
	pOpenParameter->ulAdminOffSet = dwStartOffset;
	lpString = (LPWSTR)((char*)pOpenParameter + dwStartOffset);
	wcscpy_s(lpString, pOpenParameter->ulAdminLength, VolumeOpen.strAdmin.c_str());
	dwStartOffset +=1;

	pOpenParameter->ulAdminPwdLength = (ULONG)VolumeOpen.strAdminPwd.size();
	pOpenParameter->ulAdminPwdOffSet = dwStartOffset;
	lpString = (LPWSTR)((char*)pOpenParameter + dwStartOffset);
	wcscpy_s(lpString, pOpenParameter->ulAdminPwdLength, VolumeOpen.strAdminPwd.c_str());
	dwStartOffset +=1;*/


	pOpenParameter->ulExtentsOffSet = dwStartOffset;
	PAFVOLUME_DISK_EXTENT pExts = (PAFVOLUME_DISK_EXTENT)((char*)pOpenParameter + dwStartOffset);
	for(size_t i=0; i<VolumeOpen.DiskExts.size(); i++)
	{
		pExts[i] = VolumeOpen.DiskExts[i];
	}
	dwParameterSize = (DWORD)tSize;
	return 0;
}

inline void FreePlainMemory(PAFOPEN_VVOL_PARAMETERS pOpenParameter)
{
	if(pOpenParameter)
	{
		delete [] pOpenParameter;
	}
}
#endif

/**
* Initialize and open virtual volume.
* @Param  pOpenParameter	[IN]Specify parameters about session and virtual volume;
* @Param  ulParemeterSize   [IN]Specify input parameter pOpenParameter size int byte;
* @Param  pVolHandle		[OUT]Return the opened virtual volume handle;
* @Param  psSvrInfo			[IN, OPTION]RPS server connect information. Only for remote session(AF_VSOURCE_REMOTE_SESS). Otherwise NULL;
* @Return If succeed return zero, while failed return error code.
*/
ULONG WINAPI AFOpenVolume(IN PAFOPEN_VVOL_PARAMETERS pOpenParameter, IN ULONG ulParemeterSize, OUT AFVOLHANDLE* pVolHandle, IN PST_SRVINFO psSvrInfo = NULL);

/**
* Read data from virtual volume.
* @Param  pVolHandle	[IN]Virtual volume handle;
* @Param  llOffSet		[IN]Read offset address;
* @Param  pBuffer		[IN]Buffer to receive read data;
* @Param  ulLength		[IN]Read length, in byte;
* @Param  pulReturn		[OUT]Return actually read length, in byte.
* @Return If succeed return zero, while failed return error code.
*/
ULONG WINAPI AFReadData(IN AFVOLHANDLE pVolHandle, IN LONGLONG llOffSet, IN PVOID pBuffer, IN ULONG ulLength, OUT ULONG* pulReturn);

/**
* Write data to virtual volume. Actually write to temp data file.
* @Param  pVolHandle	[IN]Virtual volume handle;
* @Param  llOffSet		[IN]Write offset address;
* @Param  pBuffer		[IN]Write data buffer;
* @Param  ulLength		[IN]Write length, in byte;
* @Param  pulReturn		[OUT]Return actually write length, in byte.
* @Return If succeed return zero, while failed return error code.
*/
ULONG WINAPI AFWriteData(IN AFVOLHANDLE pVolHandle, IN LONGLONG llOffSet, IN PVOID pBuffer, IN ULONG ulLength, OUT ULONG* pulReturn);

/**
* Close opened virtual volume.
* @Param  pVolHandle	[IN]Virtual volume handle to close.
* @Return If succeed return zero, while failed return error code.
*/
ULONG WINAPI AFCloseVolume(IN AFVOLHANDLE pVolHandle);
