#pragma once

#include <string>

using namespace std;

class CIniFileWrapper
{
private:
	wstring			m_strFilePath;
public:
	CIniFileWrapper(void);
	~CIniFileWrapper(void);

public:
	DWORD	Open(const wstring &strPath);

	DWORD	GetValue(const wstring &strSection, const wstring &strKey, wstring &strValue);
	DWORD	GetValue(const wstring &strSection, const wstring &strKey, int &nValue);
	DWORD	GetValue(const wstring &strSection, const wstring &strKey, bool &bValue);

	DWORD	SetValue(const wstring &strSection, const wstring &strKey, LPCWSTR lpszValue);
	DWORD	SetValue(const wstring &strSection, const wstring &strKey, const wstring &strValue);
	DWORD	SetValue(const wstring &strSection, const wstring &strKey, int nValue);
	DWORD	SetValue(const wstring &strSection, const wstring &strKey, bool bValue);
};
