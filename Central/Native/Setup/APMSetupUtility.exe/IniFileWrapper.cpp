#include "StdAfx.h"
#include <errno.h>
#include "IniFileWrapper.h"
#include "Utility.h"

CIniFileWrapper::CIniFileWrapper(void)
{
}

CIniFileWrapper::~CIniFileWrapper(void)
{
}

DWORD CIniFileWrapper::Open( const wstring &strPath )
{
	if(_waccess_s(strPath.c_str(), 0) == ENOENT)
		return 1;
	else{
		m_strFilePath = strPath;
		return 0;
	}
}

DWORD CIniFileWrapper::GetValue( const wstring &strSection, const wstring &strKey, wstring &strValue )
{
	WCHAR lpszBuf[512];
	const int nBuf = 512;
	DWORD dwRet = 0;

	lpszBuf[0] = L'\0';
	dwRet = GetPrivateProfileString(strSection.c_str(), strKey.c_str(), NULL, lpszBuf, nBuf, m_strFilePath.c_str());
	strValue = wstring(lpszBuf);
	
	return 0;
}

DWORD CIniFileWrapper::GetValue( const wstring &strSection, const wstring &strKey, int &nValue )
{
	nValue = GetPrivateProfileInt(strSection.c_str(), strKey.c_str(), 0, m_strFilePath.c_str());
	return 0;
}

DWORD CIniFileWrapper::GetValue( const wstring &strSection, const wstring &strKey, bool &bValue )
{
	WCHAR lpszBuf[32];
	const int nBuf = 32;
	DWORD dwRet = 0;

	lpszBuf[0] = L'\0';
	dwRet = GetPrivateProfileString(strSection.c_str(), strKey.c_str(), NULL, lpszBuf, nBuf, m_strFilePath.c_str());
	if( wcslen(lpszBuf) == 0 )
		bValue = FALSE;
	else{
		if( _wcsicmp(lpszBuf, L"1") == 0 ||
			_wcsicmp(lpszBuf, L"yes") == 0 ||
			_wcsicmp(lpszBuf, L"true") == 0 )
			bValue = true;
		else
			bValue = false;
	}
	return 0;
}

DWORD CIniFileWrapper::SetValue( const wstring &strSection, const wstring &strKey, const wstring &strValue )
{
	if (WritePrivateProfileString(strSection.c_str(), strKey.c_str(), strValue.c_str(), m_strFilePath.c_str()))
		return 0;
	else
		return 1;
}

DWORD CIniFileWrapper::SetValue( const wstring &strSection, const wstring &strKey, int nValue )
{
	WCHAR lpszBuf[32];
	const int nBuf = 32;

	_itow_s(nValue, lpszBuf, nBuf, 10);
	if (WritePrivateProfileString(strSection.c_str(), strKey.c_str(), lpszBuf, m_strFilePath.c_str()))
		return 0;
	else
		return 1;

}

DWORD CIniFileWrapper::SetValue( const wstring &strSection, const wstring &strKey, bool bValue )
{
	WCHAR lpszBuf[32];
	const int nBuf = 32;

	if( bValue )
		lpszBuf[0] = L'1';
	else
		lpszBuf[0] = L'0';
	lpszBuf[1]= L'\0';
	if (WritePrivateProfileString(strSection.c_str(), strKey.c_str(), lpszBuf, m_strFilePath.c_str()))
		return 0;
	else
		return 1;
}

DWORD CIniFileWrapper::SetValue( const wstring &strSection, const wstring &strKey, LPCWSTR lpszValue )
{
	wstring strValue = wstring(lpszValue);
	return SetValue( strSection, strKey, strValue );
}
