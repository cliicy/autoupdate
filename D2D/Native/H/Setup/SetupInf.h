/////////////////////////////////////////////////////////////////////////////
// SetupInf.h		CSetupInf Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _SETUPINF
#define _SETUPINF

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CSetupInf
#else
class __declspec(dllimport) CSetupInf
#endif
{
private:
    static const CString m_sARCSETUP_INF;
    static const CString m_sComponents;
    static const CString m_sDataMigration;
    static const CString m_sTNGFrameWork;
    static const CString m_sTNGFrameWorkSetupFileName;
    static const CString m_sProductInfo;
    static const CString m_sProductName;
    static const CString m_sSetupFileName;
    static const CString m_sCALicenseInfo;
    static const CString m_sProdSubType;
    static const CString m_sProductType;
	static const CString m_sLicInstallFileName;
	static const CString m_sChangerLicenseLevel;
	static const CString m_sLicenseTextFile;
	static const CString m_sProdBuildNoRange;

	static const CString m_sIE5InstallFileName;
	static const CString m_sConfigFileName;
	static const CString m_sMsiInstallFileName;
	static const CString m_sMsmInstallFileName;
	static const CString m_sLicComponentCode;
	// { #30032006 // added by yuani01 for SQL 2005 Express installation
	static const CString m_sSQL2005EFileName;
	// } #30032006  
	// { #070802001 yuani01 for Beta upgrade
	static const CString m_sBetaUpgradeVerName;
	// } #070802001 yuani01


    CString m_sInfFile;
    BOOL    m_bHSMAvailable;
    BOOL    m_bTNGFWAvailable;
    CString m_sTNGFWSetupFile;
    CString m_sSetupProductName;
    CString m_sSetupFile;
    BOOL    m_bExists;
    BOOL    FindFile();
	CString	m_sLicInstallFile;
	DWORD   m_dwChangerLicLevel;
	CString m_sLicenseTextFileName;

	CString	m_sIE5InstallFile;
	CString m_sConfigFile;
	CString	m_sMsiFile;
	CString	m_sMsmFile;
	CString m_sLicCompCode;

	// { #30032006
	CString m_sSQL2005EInstFileName;
	// } #30032006

public:
    CSetupInf();
	~CSetupInf();

    BOOL    IsHSMAvailable();
    BOOL    IsTNGFWAvailable();
    LPCTSTR GetTNGFWSetupFileName();
    LPCTSTR GetSetupProductName();
    LPCTSTR GetSetupFileName();
	int		GetProductSubType();
	int		GetProductType();
	LPCTSTR GetLicInstallFileName();
	DWORD   GetChangerLicLevel();
	LPCTSTR GetLicenseTextFileName();
	BOOL	GetUpdateBuildNoRange( LPCTSTR pKeyName, int* nMinBuild, int* nMaxBuild);
	BOOL	SetARCsetupINFName(LPCTSTR lpszFileName);

	LPCTSTR GetIE5InstallFileName();
	LPCTSTR GetConfigFileName();
	LPCTSTR GetMsiFileName();
	LPCTSTR GetMsmFileName();
	LPCTSTR GetLicCompCode();
//    CSetupInf(LPCTSTR lpszFileName = NULL);

	// { #30032006 yuani01
	LPCTSTR GetSQL2005EInstFileName();
	// } #30032006 yuani01
	// { #070802001 yuani01
	BOOL GetBetaUpgVer( WORD& wBetaVer );
	// } #070802001 yuani01
private:
	static const CString m_sSetup;
	static const CString m_sSupport;

};

#endif
