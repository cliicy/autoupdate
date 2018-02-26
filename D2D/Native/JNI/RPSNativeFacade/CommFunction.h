#ifndef __COMMON_FUNCTION_HEADER__
#define __COMMON_FUNCTION_HEADER__

#include "stdafx.h"
#include "AFDefine.h"

// Define in RPSCoreInterface.cpp
DWORD WINAPI RPSAFBuildBackupInfoDBWrap(WCHAR* pszBkDest, BOOL bUpdateAll);

// Define in RPSCoreInterface.cpp
DWORD WINAPI RPSAFGetAllBackupInfoDB(const wstring &strDest, BACKUP_INFO_DB &infoDb);

// Define in RPSCoreInterface.cpp
DWORD WINAPI RPSAFGetDataSizeFromShareFolder(NET_CONN_INFO netInfo, VBACKUP_NODES_SIZE& vBkNodesSize);

DWORD WINAPI RPSGetMaxStorageCapacity(std::vector<wstring>& destList, BK_STORAGE_MAX_CAP& stMaxCap);

// 
NET_CONN_INFO CreateNetConnInfo(const WCHAR* pszSource, const WCHAR* pszUsrName, const WCHAR* pszUsrPwd)
{
	NET_CONN_INFO netInfo = { 0 };

	if (pszSource)
		wcsncpy_s(netInfo.szDir, ARRAYSIZE(netInfo.szDir), pszSource, _TRUNCATE);
	if (pszUsrPwd)
		wcsncpy_s(netInfo.szPwd, ARRAYSIZE(netInfo.szPwd), pszUsrPwd, _TRUNCATE);

	if (pszUsrName != NULL)	// Following analyze username / domain.
	{
		wstring strName = pszUsrName;
		wstring strJustName = L"";
		wstring strDomain = L"";
		wstring::size_type nPos = strName.find(L'\\');
		if (nPos == wstring::npos)	// Format: user@domain
		{
			nPos = strName.find(L'@');
			if (nPos == wstring::npos){
				strJustName = strName;
			}
			else{
				strJustName = strName.substr(0, nPos);
				strDomain = strName.substr(nPos + 1);
			}
		}
		else if (nPos < 2)		// Format: \administrator or .\administrator 
		{
			strJustName = strName.substr(nPos + 1);
		}
		else					// Format: domain\user
		{
			strJustName = strName.substr(nPos + 1);
			strDomain = strName.substr(0, nPos);
		}

		if (strJustName.length() > 0)
			wcsncpy_s(netInfo.szUsr, ARRAYSIZE(netInfo.szUsr), strJustName.c_str(), _TRUNCATE);

		if (strDomain.length() > 0)
			wcsncpy_s(netInfo.szDomain, ARRAYSIZE(netInfo.szDomain), strDomain.c_str(), _TRUNCATE);
	}

	return netInfo;
}

#endif