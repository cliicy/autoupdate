#pragma once
#include "AFStorCommand.h"
#include "AFStorInterface.h"

typedef CREATE_SESS_ERR (__cdecl *pfnNewSession)(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo);
typedef CREATE_SESS_ERR (__cdecl *pfnOpenSession)(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, IN PD2D_ENCRYPTION_INFO pEncryptionInfo);
typedef CREATE_SESS_ERR (__cdecl *pfnOpenSessionEx)(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, LPWSTR pwzSessFolder, DWORD* pdwSessFolderLen, IN PD2D_ENCRYPTION_INFO pEncryptionInfo); 
typedef CREATE_SESS_ERR (__cdecl *pfnOpenSessionEx2)(LPCWSTR pszRootFolder, DWORD dwSessionNumber, OUT IAFSession** ppSession, LPWSTR pwzSessFolder, DWORD* pdwSessFolderLen, IN PD2D_ENCRYPTION_INFO pEncryptionInfo, IEventMessage* pEventMsg); 
typedef DWORD (__cdecl *pfnFindSessNumOfParentDisk)(LPCWSTR pszRootFolder, DWORD dwDiskSig);
typedef DWORD (__cdecl *pfnGetVDiskName)(LPCWSTR pszRootFolder, DWORD dwSessionNumber, DWORD dwDiskID, WCHAR* pVDiskName, int nSizeinWCHAR);

#define NewSessionName "NewSession"
#define OpenSessionName "OpenSession"
#define OpenSessionExName "OpenSessionEx"
#define OpenSessionExName2 "OpenSessionEx2"
#define FindSessNumOfParentDiskName "FindSessNumOfParentDisk"
#define GetVDiskNameName "GetVDiskName"
#define AFStor_Dll_path TEXT("AFStor.dll")