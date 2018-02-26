// UpdateCheck.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "APMSetupUtility.h"
#include "SetupSheet.h"
#include "Golbals.h"
#include "CAComponent.h"
#include <vector>
#include <atlbase.h>
#include <io.h>

#define LOG_FILE_NAME_PRE  _T("APMSetupUtility")
#define LOG_FILE_NAME_LOG  _T(".log")

#define LINK_FILE_NAME		_T("Link.ini")
#define LINK_SECTIION		_T("LinkValue")
#define LINK_KEY_WEBSITE	_T("WebSiteLink")
#define LINK_KEY_README		_T("ReadMeFile")


#define MAX_BUF_BUS			  2048

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

// CUpdateCheckApp

BEGIN_MESSAGE_MAP(CUpdateCheckApp, CWinApp)
	ON_COMMAND(ID_HELP, &CWinApp::OnHelp)
END_MESSAGE_MAP()


// CUpdateCheckApp construction
CUpdateCheckApp theApp;
//CUpdateCheckApp txx;
const CString TYPEIGORE = _T("ComponentType");

CUpdateCheckApp::CUpdateCheckApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
	m_strWorkingDir = _T("");
	m_strLanguageID = _T("1033");
	m_strTitle = _T("");
	m_bMasterSetup = FALSE;
	m_strLogFolder = _T("");
	m_strRcFile = _T("");
	m_strIniFile = _T("");
	m_strProductName = _T("");
	m_strInfFile = _T("");
	m_breboot = FALSE;
	m_brebootNow = FALSE;
	objComponents.RemoveAll();
}

CUpdateCheckApp::~CUpdateCheckApp()
{
	//AfxMessageBox(_T("xxxxxxxxxxxxxx"));
	WriteLog(_T("begain to destructor theApp."));
	for(int i = 0; i < objComponents.GetSize(); i++)
	{
		delete (CCAComponent*)objComponents[i];
	}
	objComponents.RemoveAll();
	if(GetRebootNow())
	{
		RebootSystem();
	}
}
// The one and only CUpdateCheckApp object



// CUpdateCheckApp initialization

BOOL CUpdateCheckApp::InitInstance()
{
	// InitCommonControlsEx() is required on Windows XP if an application
	// manifest specifies use of ComCtl32.dll version 6 or later to enable
	// visual styles.  Otherwise, any window creation will fail.
	INITCOMMONCONTROLSEX InitCtrls;
	InitCtrls.dwSize = sizeof(InitCtrls);
	// Set this to include all the common control classes you want to use
	// in your application.
	InitCtrls.dwICC = ICC_WIN95_CLASSES;
	InitCommonControlsEx(&InitCtrls);

	CWinApp::InitInstance();

	//AfxEnableControlContainer(); fix issue 17153693

	// Standard initialization
	// If you are not using these features and wish to reduce the size
	// of your final executable, you should remove from the following
	// the specific initialization routines you do not need
	// Change the registry key under which our settings are stored
	// TODO: You should modify this string to be something appropriate
	// such as the name of your company or organization
	SetRegistryKey(_T("Local AppWizard-Generated Applications"));

	//check the commandline
	WriteLog(_T("CUpdateCheckApp::InitInstance(): start...."));

	CString strCmdLine = m_lpCmdLine;
	if (!strCmdLine.IsEmpty())
	{
		WriteLog(_T("CUpdateCheckApp::InitInstance(): start to parse commandline(%s)"),strCmdLine);
		ProcessCommandLine();
	}
//	wstring binFolder =  L"C:\\Program Files\\CA\\ARCserve Unified Data Protection\\Management\\BIN";
	//wstring strBinEnv = L"path";
//	_wputenv_s( strBinEnv.c_str(), L"%path%;C:\\Program Files\\CA\\ARCserve Unified Data Protection\\Management\\BIN");
	//_tputenv()
	InitSetupData();
		//backup the source handle
	HINSTANCE hOld = afxCurrentResourceHandle;
	m_strRcFile.Format(_T("%s%s"), m_strWorkingDir, MSETUPRES_DLL_FILE);
	if (_taccess(m_strRcFile, 0) == -1)
	{
		//get the file from langaugeid
		if (m_strLanguageID.IsEmpty())
		{
			LANGID lidDefault = GetSystemDefaultLangID();
			m_strLanguageID.Format(_T("%d"), lidDefault);
		}

		m_strRcFile.Format(_T("%s%s\\%s"), m_strWorkingDir, m_strLanguageID, MSETUPRES_DLL_FILE);
	}

	WriteLog(_T("CUpdateCheckApp::InitInstance(): the RC dll is %s"),m_strRcFile);

		
	afxCurrentResourceHandle = LoadLibrary(m_strRcFile);
	if(!afxCurrentResourceHandle)
	{
		CString strLog;
		strLog.Format(_T("Can not loadlibrary %s"), m_strRcFile);
		AfxMessageBox(strLog);
		WriteLog(strLog);
		return FALSE;
	}

	GetComponents(theApp.m_strInfFile);
	for(int i = 0; i < theApp.objComponents.GetSize(); ++i)
	{
		theApp.m_strIniFile = ((CCAComponent*)theApp.objComponents[i])->m_strIniPath;
	}
	CBitmap bmpWatermark;
	CBitmap bmpHeader;
	
	VERIFY(bmpWatermark.LoadBitmap(IDB_BLANK));
	VERIFY(bmpHeader.LoadBitmap(IDB_WIZ97_BANNER256));
	//bmpHeader.LoadBitmap(IDB_WIZARD_BANNER);

	if (m_strProductName.IsEmpty())
	{
		m_strTitle.LoadString(IDS_SETUPWIZARD);
		m_strProductName = m_strTitle;
	}
	CSetupSheet dlg(m_strProductName, NULL, 0, bmpWatermark, NULL, bmpHeader);
	dlg.m_psh.hInstance = ::GetModuleHandle(NULL);

	if (m_bMasterSetup)
	{
		dlg.m_ctrNavigate.SetWebPageLink(GetWebPageLinkFromCfgFile());
		dlg.m_ctrNavigate.SetReadmeLink(GetReadmeLinkFromCfgFile());
	}
	else
	{
		dlg.m_ctrNavigate.ShowLinks(FALSE);
	}

	m_pMainWnd = &dlg;
	INT_PTR nResponse = dlg.DoModal();

	//restore the source handle
	afxCurrentResourceHandle = hOld;

	if (nResponse == ID_WIZFINISH)
	{
		// TODO: Place code here to handle when the dialog is
		//  dismissed with OK
	}
	else if (nResponse == IDCANCEL)
	{
		// TODO: Place code here to handle when the dialog is
		//  dismissed with Cancel
		WriteLog(_T("CUpdateCheckApp::InitInstance(): User cancel it, so no need to handle it."));
		WriteLog(_T("CUpdateCheckApp::InitInstance(): end...."));
		return FALSE; 
	}
	WriteLog(_T("CUpdateCheckApp::InitInstance(): end...."));
	// Since the dialog has been closed, return FALSE so that we exit the
	//  application, rather than start the application's message pump.
	return FALSE;
}

void CUpdateCheckApp::ProcessCommandLine()
{
	int nArgc = __argc;
	const CString strLangId = SWITCH_LANGUAGEID;
	const CString strProductName = SWITCH_PRODUCTNAME;
	const CString strLogFolder = SWITCH_LOGFOLDER;

#ifdef UNICODE
	LPCTSTR *ppArgv = (LPCTSTR*) CommandLineToArgvW(GetCommandLine(), &nArgc);
#else
	LPCTSTR *ppArgv = (LPCTSTR*) __argv;
#endif

	for (int i = 1; i < nArgc; i++)
	{
		if ((ppArgv[i][0] == _T('-')) || (ppArgv[i][0] == _T('/'))) 
		{
			// Command line switch
			CString sCmdLine = &ppArgv[i][1];

			if (sCmdLine.GetLength() > 0)
			{
				CString strBakcup = sCmdLine;
				strBakcup.MakeUpper();

				//get the product name
				if (strBakcup.Find(strLangId) == 0)
				{
					theApp.m_strLanguageID = sCmdLine.Mid(strLangId.GetLength()+1, sCmdLine.GetLength());
				}

				if(strBakcup.Find(SWITCH_M) == 0)
				{
					theApp.m_bMasterSetup = TRUE;
				}
				if (strBakcup.Find(strProductName) == 0)
				{
					theApp.m_strProductName = sCmdLine.Mid(strProductName.GetLength()+1, sCmdLine.GetLength());
				}
				if(strBakcup.Find(strLogFolder) == 0)
				{
					theApp.m_strLogFolder = sCmdLine.Mid(strLogFolder.GetLength()+1, sCmdLine.GetLength());
				}
				if(strBakcup.Find(SWITCH_REBOOT) == 0)
				{
					m_breboot = TRUE;
				}
				//get the language id
			}
		}
	}
}


void CUpdateCheckApp::AppendBackSlash(CString &sPath)
{
	sPath.TrimRight(_T("\\"));
	
	sPath += _T("\\");
}

CString CUpdateCheckApp::GetWebPageLinkFromCfgFile()
{
	CString strWebLink(_T(""));
	CString strLinkFile;
	TCHAR szTemp[MAX_PATH];
	GetTempPath(MAX_PATH, szTemp);
	strLinkFile = szTemp;
	strLinkFile.TrimRight(_T("\\"));
	strLinkFile += _T("\\");
	strLinkFile += LINK_FILE_NAME;


	::GetPrivateProfileString(LINK_SECTIION, 
		LINK_KEY_WEBSITE, 
		_T("http://www.ca.com/us/data-loss-prevention.aspx"),
		strWebLink.GetBuffer(MAX_PATH),
		MAX_PATH,
		strLinkFile);

	strWebLink.ReleaseBuffer();

	return strWebLink;
}
BOOL CUpdateCheckApp::InitSetupData()
{
	TCHAR szModule[_MAX_PATH];
	TCHAR szDrive[_MAX_DRIVE];
	TCHAR szDir[_MAX_DIR];

	memset(szModule, 0, sizeof(szModule));
	memset(szDrive, 0, sizeof(szDrive));
	memset(szDir, 0, sizeof(szDir));

	GetModuleFileName(NULL, szModule, _MAX_PATH);

	_tsplitpath_s(szModule, szDrive, _MAX_DRIVE, szDir, _MAX_DIR, NULL, 0, NULL, 0);

	m_strWorkingDir.Format(_T("%s%s"), szDrive, szDir);
	WriteLog(_T("InitSetupData(): get the current work path(%s)"), m_strWorkingDir);

	if (!m_strWorkingDir.IsEmpty())
	{
		m_strInfFile.Format(_T("%s%s"),m_strWorkingDir,INF_UpdateCheck_FILE);
	}
	else
	{
		m_strInfFile = INF_UpdateCheck_FILE;  
	}
	return TRUE;

}
CString CUpdateCheckApp::GetReadmeLinkFromCfgFile()
{
	CString strReadMeFile(_T(""));
	CString strLinkFile;
	TCHAR szTemp[MAX_PATH];
	GetTempPath(MAX_PATH, szTemp);
	strLinkFile = szTemp;
	strLinkFile.TrimRight(_T("\\"));
	strLinkFile += _T("\\");
	strLinkFile += LINK_FILE_NAME;

	::GetPrivateProfileString(LINK_SECTIION, 
		LINK_KEY_README,
		_T("..\\readme_ENU.html"),
		strReadMeFile.GetBuffer(MAX_PATH),
		MAX_PATH,
		strLinkFile);

	strReadMeFile.ReleaseBuffer();

	return strReadMeFile;
}