#pragma once
#include <Windows.h>
#include <vector>
#include "BackupCommon.h"
using namespace std;

/*
*Purpose: Get volume bitmap from .ctf file by volume and save it to file.
*@lpBkDest: [input] backup destination.
*@dwSessNum: [input] session number.
*@lpVolumeGuid: [input] volume guid.
*@strBitmapFile: [output] volume bitmap file.
*Return:
If function successes, zero will be returned.
If function fails, error code will be returned.
*/
DWORD WINAPI RPGetVolumeBitmapToFile(LPCWSTR lpBkDest, const DWORD dwSessNum, LPCWSTR lpVolumeGuid, wstring &strBitmapFile);

/*
*Purpose: Get volume bitmap from .ctf file by volume.
*@lpBkDest: [input] backup destination.
*@dwSessNum: [input] session number.
*@lpVolumeGuid: [input] volume guid.
*@StartingLcn: [input] The LCN from which the operation should start when describing a bitmap.
*@lpOutBuffer: [output] A pointer to the output buffer, a VOLUME_BITMAP_BUFFER variably sized structure.
*@nOutBufferSize: [input] The size of the output buffer, in bytes.
*@lpBytesReturned: [output] A pointer to a variable that receives the size of the data stored in the output buffer, in bytes.
*Return:
If function successes, zero will be returned.
If function fails, error code will be returned.
////////////////////////////////////////////////////////////////////////////////////////////
The behavior is similar to DeviceIoControl(FSCTL_GET_VOLUME_BITMAP):
A return value from GetLastError of ERROR_MORE_DATA indicates to the caller that the buffer was not large enough to accommodate a complete bitmap from the requested starting LCN to the last cluster on the volume.
The BitmapSize member is the number of clusters on the volume starting from the starting LCN returned in the StartingLcn member of this structure. 
For example, suppose there are 0xD3F7 clusters on the volume.If you start the bitmap query at LCN 0xA007, then both the FAT and NTFS file systems will round down the returned starting LCN to LCN 0xA000. The value returned in the BitmapSize member will be (0xD3F7 – 0xA000), or 0x33F7.
////////////////////////////////////////////////////////////////////////////////////////////
*/
DWORD WINAPI RPGetVolumeBitmapToMemory(LPCWSTR lpBkDest, const DWORD dwSessNum, LPCWSTR lpVolumeGuid, LARGE_INTEGER StartingLcn, LPVOID lpOutBuffer, DWORD nOutBufferSize, LPDWORD lpBytesReturned);

/*
*Purpose: Get volume disk extents from .ctf file. It will allocate memory for pVolInfos, the caller need to release it.
*@lpBkDest: [input] backup destination.
*@dwSessNum: [input] session number.
*@pVolInfos: [output] volume disk extent info.
*Return:
If function successes, zero will be returned.
If function fails, error code will be returned.
*/
DWORD WINAPI RPGetVolDiskExtInfoAll(LPCWSTR lpBkDest, const DWORD dwSessNum, vector<PVM_VOL_INFO> &pVolInfos);

/*
*Purpose: Get volume disk extent from .ctf file for a volume. It will allocate memory for pVolInfo, the caller need to release it.
*@lpBkDest: [input] backup destination.
*@dwSessNum: [input] session number.
*@lpVolumeGuid: [input] volume guid.
*@pVolInfo: [output] volume disk extent info.
*Return:
If function successes, zero will be returned.
If function fails, error code will be returned.
*/
DWORD WINAPI RPGetVolDiskExtInfoByVolume(LPCWSTR lpBkDest, const DWORD dwSessNum, LPCWSTR lpVolumeGuid, VM_VOL_INFO** ppVolInfo);

/*
*Purpose: Get the session encryption information from backupinfo.xml
*@lpBkDest: [input] backup destination.
*@dwSessNum: [input] session number.
*@pdwEncFlag: [input/output] the session encryption flags. 
*@pSessEncInfo: [input/output] the session ecnryption information. It can be ignored if this parameter is NULL
*Return:
If function successes, zero will be returned.
If function fails, error code will be returned.
*/
#define SESS_ENC_FLAG_NONE				0x0000	// without password and without encryption
#define SESS_ENC_FLAG_PWD_PROTECTED		0x0001	// session is password protected
#define SESS_ENC_FLAG_ENCRYPTED			0x0002	// session is encrpted
/*
* the data of this struct is got from "backupinfo.xml"
*/
typedef struct _SESS_ENC_INFO_
{
	WCHAR szPasswordHash[MAX_PATH];		// the hash key of session password.
	WCHAR szEncryptKeyHash[MAX_PATH];	// the hash key of passwor used to encrypt data
	DWORD dwEncryptType;				// the session encrypt type

	_SESS_ENC_INFO_(){
		ZeroMemory(szPasswordHash, sizeof(szPasswordHash));
		ZeroMemory(szEncryptKeyHash, sizeof(szEncryptKeyHash));
		dwEncryptType = 0;
	}
}SESS_ENC_INFO, *PSESS_ENC_INFO;
DWORD WINAPI RPGetSessionEncryptInfo(LPCWSTR lpBkDest, const DWORD dwSessNum, PDWORD pdwEncFlag, PSESS_ENC_INFO pSessEncInfo=NULL);
DWORD WINAPI RPGetSessionEncryptInfo2(LPCWSTR lpSessionFolder, PDWORD pdwEncFlag, PSESS_ENC_INFO pSessEncInfo = NULL);