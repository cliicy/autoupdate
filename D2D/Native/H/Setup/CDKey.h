/////////////////////////////////////////////////////////////////////////////
// CDKey.h		CCDKey Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _CDKEY
#define _CDKEY

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CCDKey
#else
class __declspec(dllimport) CCDKey
#endif
{
private:
	CString m_sCDKey;
	DWORD	m_dwUserLevel;
	DWORD   m_dwProductCode;
	DWORD   m_dwOEMCode;
	DWORD	m_dwLicenseVersion;
	TCHAR   m_szSerialNumber[16];
	BOOL    m_bValidKey;

public:
	CCDKey(LPCTSTR lpszCDKey = NULL);
	void    CDKey(LPCTSTR lpszKey);
	LPCTSTR CDKey()          { return m_sCDKey; }
	DWORD   UserLevel()      { return m_dwUserLevel; }
	DWORD   ProductCode()    { return m_dwProductCode; }
	DWORD	OEMCode()	     { return m_dwOEMCode; }
	DWORD   LicenseVersion() { return m_dwLicenseVersion; }
	LPCTSTR SerialNumber()   { return m_szSerialNumber; }

	static const int TOTAL_KEYCODE_CHARS;

	enum{
		success,
		invalidKey,
		betaKey,
		invalidUserLevel,
		invalidProduct,
		invalidOEM,
		invalidVersion,
		workstationOnly,
		trialKey,
		notUpgradable,
	};

	int  ValidARCservePreviousKey();                                    //checks if key can be upgraded
	int  ValidARCserveKey(int nProductCodes, LPDWORD lpdwProductCodes);	//checks if key is a valid ARCserve new install key
	BOOL ValidARCserveUpgradeKey();	                                    //checks if key is a valid ARCserve upgrade key.
	BOOL ValidARCserveUpgrade(DWORD dwPrevProductCode, DWORD dwPrevUserLevel);

	static BOOL Decrypt(LPCTSTR lpszCDKey, LPTSTR lpszSerialNumber, LPDWORD lpdwUserLevel, LPDWORD lpdwLicenseVersion, LPDWORD lpdwProductCode, LPDWORD lpdwOEMCode);
};

#endif
