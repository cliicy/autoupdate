// UpdateCheck.h : main header file for the PROJECT_NAME application
//
#pragma once

#ifndef __AFXWIN_H__
	#error "include 'stdafx.h' before including this file for PCH"
#endif

//#include "EdgeSetupDefine.h"
#include <vector>
#include "resourceUC.h"		// main symbols
#include "resource.h"

using namespace std;
// CUpdateCheckApp:
// See UpdateCheck.cpp for the implementation of this class
//

/****
usage:
UpdateCheck.exe /Q /PRODUCTNAME="CA ARCServe Backup" /LANGUAGEID=1033 /M /INSTALL or /REMOVE

/Q   -- silent
/PRODUCTNAME  -- product name for Dialog caption, not for /REMOVE
/LANGUAGEID  -- langauge id, reqired for /INSTALL, not for /REMOVE
/M -- stand for this file is launched by mastersetup

E.g.:
UpdateCheck.exe /PRODUCTNAME="CA ARCServe Backup" /LANGUAGEID=1033 /INSTALL


****/

#define MAX_LIST_ITEM  100
#define INF_UpdateCheck_FILE          _T("APMSetupUtility.inf")
#define LOG_FILE                      _T("APMSetupUtility.log")
#define MSETUPRES_DLL_FILE _T("APMSetupUtilityRes.dll")

#define SWITCH_PRODUCTNAME  _T("PRODUCTNAME")
#define SWITCH_LANGUAGEID  _T("LANGUAGEID")
#define SWITCH_REBOOT      _T("REBOOT")
#define SWITCH_M _T("M")
#define SWITCH_LOGFOLDER          _T("LOGFOLDER")

#define INF_UPDATECHECK_SETUP     _T("SETUP")
#define INF_UPDATECHECK_UINAME    _T("SetupUIName")


class CUpdateCheckApp : public CWinApp
{
public:
	CUpdateCheckApp();
	~CUpdateCheckApp();

// Overrides
	public:
	virtual BOOL InitInstance();
	BOOL GetRebootNow()
	{
		return m_brebootNow;
	}
	void SetRebootNow(BOOL m_breboot)
	{
		m_brebootNow = m_breboot;
	}
	CString GetTitle()
	{
		return m_strTitle;
	}
// Implementation

	DECLARE_MESSAGE_MAP()
public:

	CString m_strWorkingDir;
	CString m_strLanguageID;
	CString m_strInfFile;
	CString m_strLogFolder;
	CString m_strRcFile;
	CString m_strProductName;
	BOOL m_bMasterSetup;
	CPtrArray objComponents;
	CString m_strLogFile;
	CString m_strIniFile;
	BOOL m_breboot;
	//funtion

	void ProcessCommandLine();
	BOOL InitSetupData();

	//virtual int ExitInstance();
	//functions
	
	void AppendBackSlash(CString &sPath);
	CString GetReadmeLinkFromCfgFile();
	CString GetWebPageLinkFromCfgFile();
private:
	BOOL m_brebootNow;
	CString m_strTitle;
};
//extern CUpdateCheckApp theApp;

