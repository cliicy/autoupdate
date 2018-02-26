// APMUtility.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "APMUtility.h"
#include "APMUtilityDlg.h"
#include "APMSettingDlg.h"
#include "Utility.h"
#include "Log.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CAPMUtilityApp

BEGIN_MESSAGE_MAP(CAPMUtilityApp, CWinApp)
	ON_COMMAND(ID_HELP, &CWinApp::OnHelp)
END_MESSAGE_MAP()


// CAPMUtilityApp construction

CAPMUtilityApp::CAPMUtilityApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
	
}


// The one and only CAPMUtilityApp object

CAPMUtilityApp theApp;


// CAPMUtilityApp initialization

BOOL CAPMUtilityApp::InitInstance()
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

	AfxEnableControlContainer();

	// Standard initialization
	// If you are not using these features and wish to reduce the size
	// of your final executable, you should remove from the following
	// the specific initialization routines you do not need
	// Change the registry key under which our settings are stored
	// TODO: You should modify this string to be something appropriate
	// such as the name of your company or organization
	SetRegistryKey(_T("Local AppWizard-Generated Applications"));
	
	wstring logPath;
	GetEdgeRootDir(logPath);
	logPath += wstring(FOLDER_LOG) + L"\\" + L"SetupAPMUtility.log";
	initLogModule(logPath);

	LogError( L"utility begin------------------------" );

	//set the environment variable path for loading D2D&EDGE library
	wstring binFolder;
	GetEdgeRootDir(binFolder);
	binFolder += FOLDER_BIN;
	wstring strBinEnv = L"path=%path%;" + binFolder;
	_wputenv( strBinEnv.c_str() );

	HINSTANCE handle = LoadLibrary( L"APMSetupUtilityRes.dll" );
	if( handle == NULL ){
		return FALSE;
	}
	AfxSetResourceHandle(handle);

	//get the command line parameter
	wstring strCmd = wstring(this->m_lpCmdLine);
	LPWSTR *szArglist = NULL;
	int nArgs = 0;
	vector<wstring> vecKey;
	vector<wstring> vecVal;
	m_strLanguageId = L"1033";
	m_bRebootRequired = FALSE;
	g_bContinuousMode = false;

	if( GetOpt( m_lpCmdLine, vecKey, vecVal ) == 0 ){
		for( int i = 0; i < vecKey.size(); i++ ){
			if( vecKey[i] == L"LanguageID" )
				m_strLanguageId = vecVal[i];
			if( vecKey[i] == L"Reboot"){
				if( vecVal[i] == L"0" )
					m_bRebootRequired = FALSE;
				else
					m_bRebootRequired = TRUE;
			}
			else if (vecKey[i] == L"c")
			{
				g_bContinuousMode = TRUE;
			}
		}
	}

	CAPMSettingDlg dlg;
	m_pMainWnd = &dlg;

	if (!g_bContinuousMode)
	{
		dlg.DoModal();
	}
	else
	{
		LogInfo(L"Continuous mode...");
		dlg.Create(CAPMSettingDlg::IDD);
		dlg.OnBnClickedButton3();
		dlg.RunModalLoop();
	}

	// Since the dialog has been closed, return FALSE so that we exit the
	//  application, rather than start the application's message pump.
	return FALSE;
}

int CAPMUtilityApp::ExitInstance()
{
	return CWinApp::ExitInstance();
}

std::wstring CAPMUtilityApp::GetLanguageId()
{
	return m_strLanguageId;
}

BOOL CAPMUtilityApp::IsRebootRequired()
{
	return m_bRebootRequired;
}
