// UpdateCheckDlg.cpp : implementation file
//

#include "stdafx.h"
#include "APMSetupUtility.h"
#include "APMSetupUtilityDlg.h"
#include "APMThread.h"
#include "Golbals.h"
#include "CAComponent.h"
#include "SetupSheet.h"
#include "Utility.h"
#include <atlrx.h>

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

extern CUpdateCheckApp theApp;

#define TIME_ID_BRING2TOP 10001

IMPLEMENT_DYNCREATE(CUpdateCheckPage, CSetupPage)

CUpdateCheckPage::CUpdateCheckPage() : CSetupPage(CUpdateCheckPage::IDD, 0, 0, 0)
{
	m_nNagivageTitleID = IDS_STRING_CHECKFORUPDATE_SUBTITLE;
	m_bProCtrlExist = FALSE;
	m_hCancelUpdateEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
	//GetEdgeRootDir(m_strIniFilePath);
	//m_strD2DIniFilePath = m_strIniFilePath;	
	//m_strIniFilePath += wstring(FOLDER_UPDATEMANAGER) + L"\\" + FOLDER_UPDATEMANAGER_ARCAPP + L"\\" + 
	//	FILE_EDGE_PMSETTING_INI;	
	//m_strD2DIniFilePath += wstring(FOLDER_UPDATEMANAGER) + L"\\" + FILE_D2D_PMSETTING_INI;
	m_strCaption.LoadString(IDS_TITLE_MESSBOX);
	RadioStatus = 0;
}

CUpdateCheckPage::~CUpdateCheckPage()
{
	CloseHandle(m_hCancelUpdateEvent);
}

void CUpdateCheckPage::DoDataExchange(CDataExchange* pDX)
{
	CSetupPage::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_RADIO_CASERVER, m_radioCaserver);
	DDX_Control(pDX, IDC_BUTTON_CHECK, m_checkbutton);
	DDX_Control(pDX, IDC_RADIO_STAGIN, m_radioStagin);
	DDX_Control(pDX, IDC_RADIO_STAGIN2, m_radioStagin2);
	DDX_Control(pDX, IDC_UPDATES_DES, m_lblupdatesdes);
	DDX_Control(pDX, IDC_STATIC_GROUP1, groupboxCaserver);
	DDX_Control(pDX, IDC_STATIC_GROUP2, groupboxStagin);
	DDX_Control(pDX, IDC_CHECK_PROXY, m_ckProxy);
	DDX_Control(pDX, IDC_LBL_PROXYSERVER, m_lblProxyServer);
	DDX_Control(pDX, IDC_LBL_PROXYPORT, m_lblProxyPort);
	DDX_Control(pDX, IDC_EDIT_PROXY_SERVER, m_tbProxyServer);
	DDX_Control(pDX, IDC_EDIT_PROXY_PORT, m_tbProxyPort);
	DDX_Control(pDX, IDC_CHECK_PROXY_AUTH, m_ckProxyAuth);
	DDX_Control(pDX, IDC_LBL_PROXY_USER, m_lblProxyUser);
	DDX_Control(pDX, IDC_EDIT_PROXY_USER, m_tbProxyUser);
	DDX_Control(pDX, IDC_EDIT_PROXY_PWD, m_tbProxyPwd);
	DDX_Control(pDX, IDC_LBL_PROXY_PWD, m_lblProxyPwd);
	DDX_Control(pDX, IDC_LBL_STAGING_SERVER, m_lblStagingServer);
	DDX_Control(pDX, IDC_LBL_STAGING_PORT, m_lblStagingPort);
	DDX_Control(pDX, IDC_EDIT_STAGING_SERVER, m_tbStagingServer);
	DDX_Control(pDX, IDC_EDIT_STAGING_PORT, m_tbStagingPort);
	DDX_Control(pDX, IDC_STATIC_TITIL, m_tbtital);
	DDX_Control(pDX, IDC_STATIC_DES, m_tbdes);
	DDX_Control(pDX, IDC_STATIC_PROGRESS, m_tbprocess);
	DDX_Radio(pDX, IDC_RADIO_CASERVER, m_apmSettingModel.m_nServerType);
	DDX_Check(pDX, IDC_CHECK_PROXY, m_apmSettingModel.m_bProxy);
	DDX_Text(pDX, IDC_EDIT_PROXY_SERVER, m_apmSettingModel.m_proxyServer.m_strServer);
	DDX_Text(pDX, IDC_EDIT_PROXY_PORT, m_apmSettingModel.m_proxyServer.m_strPort);
	DDV_MaxChars(pDX, m_apmSettingModel.m_proxyServer.m_strPort, 5);
	DDX_Check(pDX, IDC_CHECK_PROXY_AUTH, m_apmSettingModel.m_bProxyAuth);
	DDX_Text(pDX, IDC_EDIT_PROXY_USER, m_apmSettingModel.m_strProxyUsername);
	DDX_Text(pDX, IDC_EDIT_PROXY_PWD, m_apmSettingModel.m_strProxyPwd);
	DDX_Text(pDX, IDC_EDIT_STAGING_SERVER, m_apmSettingModel.m_stagingServer.m_strServer);
	DDX_Text(pDX, IDC_EDIT_STAGING_PORT, m_apmSettingModel.m_stagingServer.m_strPort);
	DDV_MaxChars(pDX, m_apmSettingModel.m_stagingServer.m_strPort, 5);
	//{{AFX_DATA_MAP(CUpdateCheckPage
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CUpdateCheckPage, CSetupPage)
	//{{AFX_MSG_MAP(CUpdateCheckPage)
	ON_WM_TIMER()
	ON_MESSAGE(WM_APM_STATUS_CHANGED, &CUpdateCheckPage::OnAPMStatusChanged)
	ON_BN_CLICKED(IDC_CHECK_PROXY, &CUpdateCheckPage::OnBnClickedCaProxyServer)
	ON_BN_CLICKED(IDC_RADIO_CASERVER, &CUpdateCheckPage::OnBnClickedRadioCaServer)
	ON_BN_CLICKED(IDC_CHECK_PROXY_AUTH, &CUpdateCheckPage::OnBnClickedCheckProxyAuth)
	ON_BN_CLICKED(IDC_RADIO_STAGIN, &CUpdateCheckPage::OnBnClickedRadioStagin)
	ON_BN_CLICKED(IDC_RADIO_STAGIN2, &CUpdateCheckPage::OnBnClickedRadioStagin2)
	ON_BN_CLICKED(IDC_BUTTON_CHECK, &CUpdateCheckPage::OnCheckingUpdate)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CUpdateCheckPage message handlers

BOOL CUpdateCheckPage::OnInitDialog() 
{
	CSetupPage::OnInitDialog();	
	CSetupSheet* pSheet = (CSetupSheet*)GetParent();
	ASSERT_KINDOF(CPropertySheetEx, pSheet);
	CWnd* pWnd = GetDlgItem(IDC_PROGRESS1_PIC);
	pWnd->ShowWindow(FALSE);

	m_tbtital.SetFontBold(TRUE);
	//m_tbprocess.ShowWindow(FALSE);
	for(int i = 0; i <  theApp.objComponents.GetSize(); i++)
	{
		if (((CCAComponent*)theApp.objComponents[i])->LoadApmSetting(m_apmSettingModel))
		{
			break;
		}
		//break;
	}

	UpdateData(FALSE);

	if (m_apmSettingModel.m_nServerType == APM_SERVERTYPE_CA)
	{
		SetRadioStatus(0);
		m_radioCaserver.SetCheck(1);
		EnableProxyControls(TRUE);
		EnableStagingControls(FALSE);
		m_radioStagin.ShowWindow(FALSE);
		m_radioStagin2.ShowWindow(TRUE);
	}
	else
	{
		SetRadioStatus(2);
		EnableProxyControls(FALSE);
		//EnableStagingControls(TRUE);
		m_radioStagin.ShowWindow(TRUE);
		m_radioStagin.SetCheck(1);
		m_radioStagin2.ShowWindow(FALSE);
	}

	//Resize the button
	pWnd = GetDlgItem(IDC_BUTTON_CHECK);
	if (pWnd)
	{
		CSize sizeText;
		CClientDC dc(this);
		CFont *pFont = this->GetFont();
		CFont* pOldFont = dc.SelectObject(pFont);
	
		CRect r;
		pWnd->GetWindowRect(&r);
		ScreenToClient(&r);

		CString strMsg;
		pWnd->GetWindowText(strMsg);
		sizeText = dc.GetTextExtent(strMsg);

		r.right = r.left + sizeText.cx + 20;
		pWnd->SetWindowPos(NULL, r.left, r.top, r.Width(), r.Height(), SWP_NOMOVE | SWP_NOZORDER);

		dc.SelectObject(pOldFont);
	}

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

BOOL CUpdateCheckPage::OnSetActive() 
{
	BOOL blRet = CSetupPage::OnSetActive();

	CSetupSheet* pSheet = (CSetupSheet*)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	//pSheet->SetWizardButtons(PSWIZB_DISABLEDFINISH);

	CWnd *pwnd = pSheet->GetDlgItem(ID_WIZBACK);
	if (pwnd)
	{
		pwnd->ShowWindow(SW_HIDE);
	}
	
	pwnd = pSheet->GetDlgItem(ID_WIZNEXT);
	if (pwnd)
	{
		CString	strText;
		strText.LoadStringW(IDS_STRING_UPDATE_BUTTON);
		pwnd->SetWindowText(strText);
	}
	return blRet;
}

LRESULT CUpdateCheckPage::OnWizardNext()
{
	CSetupSheet* pSheet = (CSetupSheet*)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);
	OnCheckingUpdate();
	return -1;
}

BOOL CUpdateCheckPage::OnQueryCancel()
{
	CSetupSheet* pSheet = (CSetupSheet*)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);
	CString sText;
	sText.LoadString(IDS_SETUP_CANCEL);
	if (IDYES == MessageBox(sText, m_strCaption, MB_YESNO | MB_ICONQUESTION))
	{
		WriteLog(_T("User cancelled installation"));
		if (theApp.m_breboot)
		{
			CString	strText;
			strText.LoadStringW(IDS_REBOOT_SYSTEM_ON_DEMAND);
			if (IDYES == MessageBox(strText, m_strCaption, MB_YESNO | MB_ICONQUESTION))
			{
				theApp.SetRebootNow(TRUE);
				WriteLog(_T("User selected YES to reboot machine"));
			}
		}
		//CString strCaption;
		return TRUE;
		//strCaption.LoadStringW(IDS_STRING_CHECKFORUPDATE_SUBTITLE);
	}
	return FALSE;
}

BOOL CUpdateCheckPage::OnWizardFinish() 
{
	CSetupSheet* pSheet = (CSetupSheet*)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	//check the radio selection
	if (theApp.m_breboot)
	{
		//CString strCaption;
		//strCaption.LoadStringW(IDS_STRING_CHECKFORUPDATE_SUBTITLE);
		CString	strText;
		strText.LoadStringW(IDS_REBOOT_SYSTEM_ON_DEMAND);
		if (IDYES == MessageBox(strText, m_strCaption, MB_YESNO | MB_ICONQUESTION))
		{
			theApp.SetRebootNow(TRUE);
			WriteLog(_T("User selected YES to reboot machine"));
		}
	}
	return CSetupPage::OnWizardFinish();
}

HANDLE CUpdateCheckPage::GetCancelUpdateEvent()
{
	return m_hCancelUpdateEvent;
}

void CUpdateCheckPage::OnBnClickedCaProxyServer()
{
	if (m_ckProxy.GetCheck() == BST_CHECKED)
	{
		m_tbProxyServer.EnableWindow(TRUE);
		m_tbProxyPort.EnableWindow(TRUE);
		m_ckProxyAuth.EnableWindow(TRUE);
		m_lblProxyServer.EnableWindow(TRUE);
		m_lblProxyPort.EnableWindow(TRUE);
		EnableProxyAuthControls(FALSE);
	}
	else
	{
		m_ckProxyAuth.SetCheck(BST_UNCHECKED);
		m_tbProxyServer.EnableWindow(FALSE);
		m_tbProxyPort.EnableWindow(FALSE);
		m_ckProxyAuth.EnableWindow(FALSE);
		m_lblProxyServer.EnableWindow(FALSE);
		m_lblProxyPort.EnableWindow(FALSE);
		EnableProxyAuthControls(FALSE);
	}
}

void CUpdateCheckPage::OnBnClickedRadioCaServer()
{
	//m_radioCaserver.SetCheck(1);
	if (GetRadioStatus() == 0)
	{
		return;
	}
	SetRadioStatus(0);
	//UpdateData(FALSE);
	EnableProxyControls(TRUE);
	EnableStagingControls(FALSE);
	m_radioStagin2.ShowWindow(TRUE);
	m_radioStagin.ShowWindow(FALSE);
}

void CUpdateCheckPage::OnBnClickedRadioStagin()
{
	if (GetRadioStatus() == 1)
	{
		return ;
	}
	SetRadioStatus(1);
}

void CUpdateCheckPage::OnBnClickedRadioStagin2()
{
	if (GetRadioStatus() == 2)
	{
		return ;
	}
	SetRadioStatus(2);
	//UpdateData(TRUE);
	m_radioStagin.ShowWindow(TRUE);
	m_radioStagin.SetCheck(1);
	m_radioStagin2.SetCheck(0);
	m_radioStagin2.ShowWindow(FALSE);
	EnableProxyControls(FALSE);
	EnableStagingControls(TRUE);
}

void CUpdateCheckPage::EnableAllControls(BOOL bEnable)
{
	if (bEnable)
	{
		m_radioCaserver.ShowWindow(TRUE);
		if (GetRadioStatus() != 0)
		{
			m_radioStagin.ShowWindow(TRUE);
			EnableProxyControls(FALSE);
			EnableStagingControls(TRUE);
		}
		else
		{
			m_radioStagin2.ShowWindow(TRUE);
			EnableProxyControls(TRUE);
			EnableStagingControls(FALSE);
		}
	}
	else
	{
		m_radioCaserver.ShowWindow(FALSE);
		if (GetRadioStatus() != 0)
		{
			m_radioStagin.ShowWindow(FALSE);
			EnableStagingControls(FALSE);
		}
		else
		{
			m_radioStagin2.ShowWindow(FALSE);
			EnableProxyControls(FALSE);
		}
	}
}

void CUpdateCheckPage::EnableStagingControls(BOOL bEnable)
{
	if (bEnable)
	{
		groupboxStagin.ShowWindow(TRUE);
		m_tbStagingServer.ShowWindow(TRUE);
		m_tbStagingPort.ShowWindow(TRUE);
		m_lblStagingServer.ShowWindow(TRUE);
		m_lblStagingPort.ShowWindow(TRUE);

		m_tbStagingServer.EnableWindow(TRUE);
		m_tbStagingPort.EnableWindow(TRUE);
		m_lblStagingServer.EnableWindow(TRUE);
		m_lblStagingPort.EnableWindow(TRUE);
	}
	else
	{
		groupboxStagin.ShowWindow(FALSE);
		m_tbStagingServer.ShowWindow(FALSE);
		m_tbStagingPort.ShowWindow(FALSE);
		m_lblStagingServer.ShowWindow(FALSE);
		m_lblStagingPort.ShowWindow(FALSE);
	}
}

void CUpdateCheckPage::OnBnClickedCheckProxyAuth()
{
	if (m_ckProxyAuth.GetCheck() == BST_CHECKED)
	{
		EnableProxyAuthControls(TRUE);
	}
	else
	{
		EnableProxyAuthControls(FALSE);
	}
}

void CUpdateCheckPage::EnableProxyControls(BOOL bEnable)
{
	if (bEnable)
	{
		m_ckProxy.ShowWindow(TRUE);

		groupboxCaserver.ShowWindow(TRUE);
		m_tbProxyServer.ShowWindow(TRUE);
		m_tbProxyPort.ShowWindow(TRUE);
		m_ckProxyAuth.ShowWindow(TRUE);
		m_lblProxyServer.ShowWindow(TRUE);
		m_lblProxyPort.ShowWindow(TRUE);

		ShowProxyAuthControls(TRUE);
		if (m_ckProxy.GetCheck() != BST_CHECKED)
		{
			m_tbProxyServer.EnableWindow(FALSE);
			m_tbProxyPort.EnableWindow(FALSE);
			m_ckProxyAuth.SetCheck(FALSE);
			m_ckProxyAuth.EnableWindow(FALSE);
			m_lblProxyServer.EnableWindow(FALSE);
			m_lblProxyPort.EnableWindow(FALSE);
			EnableProxyAuthControls(FALSE);
		}

		if (m_ckProxyAuth.GetCheck() != BST_CHECKED)
		{
			EnableProxyAuthControls(FALSE);
		}
	}
	else
	{
		m_ckProxy.ShowWindow(FALSE);
		groupboxCaserver.ShowWindow(FALSE);
		m_tbProxyServer.ShowWindow(FALSE);
		m_tbProxyPort.ShowWindow(FALSE);
		m_ckProxyAuth.ShowWindow(FALSE);
		m_lblProxyServer.ShowWindow(FALSE);
		m_lblProxyPort.ShowWindow(FALSE);
		ShowProxyAuthControls(FALSE);
	}
}

void CUpdateCheckPage::EnableProxyAuthControls(BOOL bEnable)
{
	if (bEnable)
	{
		m_tbProxyUser.EnableWindow(TRUE);
		m_tbProxyPwd.EnableWindow(TRUE);
		m_lblProxyUser.EnableWindow(TRUE);
		m_lblProxyPwd.EnableWindow(TRUE);
	}
	else
	{
		m_tbProxyUser.EnableWindow(FALSE);
		m_tbProxyPwd.EnableWindow(FALSE);
		m_lblProxyUser.EnableWindow(FALSE);
		m_lblProxyPwd.EnableWindow(FALSE);
	}
}

void CUpdateCheckPage::ShowProxyAuthControls(BOOL bEnable)
{
	if (bEnable)
	{
		m_tbProxyUser.ShowWindow(TRUE);
		m_tbProxyPwd.ShowWindow(TRUE);
		m_lblProxyUser.ShowWindow(TRUE);
		m_lblProxyPwd.ShowWindow(TRUE);
	}
	else
	{
		m_tbProxyUser.ShowWindow(FALSE);
		m_tbProxyPwd.ShowWindow(FALSE);
		m_lblProxyUser.ShowWindow(FALSE);
		m_lblProxyPwd.ShowWindow(FALSE);
	}
}

void CUpdateCheckPage::OnCheckingUpdate()
{
	//return;
	CSetupSheet* pSheet = (CSetupSheet*)GetParent();
	ASSERT_KINDOF(CPropertySheetEx, pSheet);
	UpdateData(TRUE);
	pSheet->RemoveAllMessage();
	//APMSetting apmSetting;
	m_apmSetting = ConvertFromModel(m_apmSettingModel);
	WriteLog(_T("The parameters:%d, %s, %s, %s"), m_apmSettingModel.m_nServerType, m_apmSettingModel.m_proxyServer.m_strServer, m_apmSettingModel.m_stagingServer.m_strServer, m_apmSettingModel.m_stagingServer.m_strPort);

	if (Validate())
	{
		for(int i = 0; i <  theApp.objComponents.GetSize(); i++)
		{
			((CCAComponent*)theApp.objComponents[i])->SaveApmSetting(m_apmSettingModel);
			break;
		}

		CString strSta, strDes;
		m_checkbutton.ShowWindow(SW_HIDE);
		strSta.LoadString(IDS_PATCH_INSTALL);
		strDes.LoadString(IDS_PATH_INSTALL_DES);
		//m_tbtital.SetWindowText(strSta);
		m_tbdes.SetWindowText(strDes);
		CSetupSheet* pSheet = (CSetupSheet*)GetParent();
		ASSERT_KINDOF(CPropertySheetEx, pSheet);
		//strSta.LoadString(IDS_UPDATE_PROCESS);
		strDes.LoadString(IDS_BEGIN_CHECK);
		//m_tbprocess.SetWindowText(strSta);
		m_lblupdatesdes.SetWindowText(strSta);
		//cxl
		/*CWnd* pwnd = pSheet->GetDlgItem(IDCANCEL);
		if (pwnd)
		{
			pwnd->EnableWindow(FALSE);
		}*/
		CWnd* pwnd = pSheet->GetDlgItem(ID_WIZNEXT);
		if (pwnd)
		{
			pwnd->EnableWindow(FALSE);
		}
		//
		if (!isProcessCtrlExist())
		{
			CWnd* pWnd = GetDlgItem(IDC_PROGRESS1_PIC);
			if (pWnd)
			{
				CRect rect;
				pWnd->GetWindowRect(&rect);
				ScreenToClient(&rect);
				m_ctrlProgress.Create(WS_VISIBLE | WS_CHILD | PBS_SMOOTH, rect, this, IDC_PROGRESS_UPDATE);
				SetProcessCtrlExit(TRUE);
				m_ctrlProgress.SetRange(0, 100);
			}
		}
		else
		{
			m_ctrlProgress.ShowWindow(TRUE);
		}

		EnableAllControls(FALSE);
		m_ctrlProgress.SetPos(5);
		CreateThread(0, 0, RunApm, this, 0, 0);
	}
}

afx_msg LRESULT CUpdateCheckPage::OnAPMStatusChanged(WPARAM wParam, LPARAM lParam)
{
	CSetupSheet* pSheet = (CSetupSheet*)GetParent();
	ASSERT_KINDOF(CPropertySheetEx, pSheet);
	CString strStep, strError, strStaUpdate, strdes;
	wstring wstrrespon;
	CWnd* pwnd ;
	switch(lParam)
	{
	case APM_STATUS_INSTALL_FAILED:
		m_ctrlProgress.SetPos(100);
		pSheet->SetWizardButtons(PSWIZB_FINISH);
		pwnd = pSheet->GetDlgItem(IDCANCEL);
		if (pwnd)
		{
			pwnd->EnableWindow(FALSE);
		}
		strStep.LoadString(IDS_ERROR_INSTALL);
		m_tbdes.SetWindowText(strStep);
		strdes.LoadString(IDS_INST_FAILED);
		//m_tbtital.SetWindowText(strdes);
		strError.LoadString(IDS_ERROR_INSTALL_CODE);
		strError.Format(strError, wParam);
		m_lblupdatesdes.SetWindowText(strError);		
		break; 
	case APM_STATUS_INSTALL_FINISHED:
		m_ctrlProgress.SetPos(100);
		pSheet->SetWizardButtons(PSWIZB_FINISH);
		pwnd = pSheet->GetDlgItem(IDCANCEL);
		if (pwnd)
		{
			pwnd->EnableWindow(FALSE);
		}
		strStep.LoadString(IDS_APM_STEP_FINISH);
		//m_tbtital.SetWindowText(strStep);
		m_lblupdatesdes.SetWindowText(strStep);
		strStaUpdate.LoadString(IDS_UPDATE_FINISH);
		m_tbdes.SetWindowText(strStaUpdate);
		break;
	case APM_STATUS_ERROR:
		//m_ctrlProgress.ShowWindow(FALSE);
		//m_checkbutton.ShowWindow(TRUE);
		//m_tbprocess.SetWindowText(_T(""));
		//m_lblupdatesdes.SetWindowText(_T(""));
		//EnableAllControls(TRUE);
		wstrrespon = (WCHAR*)wParam;
		strError.Format(_T("%s"), wstrrespon.c_str());
		pSheet->AddMessage(strError, MSG_TYPE_ERROR);

		//strStaUpdate.LoadString(IDS_STATIC_CHECKUPDATE);
		strdes.LoadString(IDS_DES_CHECKUPDATE);
		//m_tbtital.SetWindowText(strStaUpdate);
		m_tbdes.SetWindowText(strdes);
		m_ctrlProgress.ShowWindow(FALSE);
		//m_checkbutton.ShowWindow(TRUE);
		//m_tbprocess.SetWindowText(_T(""));
		m_lblupdatesdes.SetWindowText(_T(""));
		EnableAllControls(TRUE);
		//m_checkbutton.ShowWindow(SW_SHOW);

		pwnd = pSheet->GetDlgItem(ID_WIZNEXT);
		if (pwnd)
		{
			pwnd->EnableWindow(TRUE);
		}
		pwnd = pSheet->GetDlgItem(IDCANCEL);
		if (pwnd)
		{
			pwnd->EnableWindow(TRUE);
		}

		break;
	case APM_STATUS_PROCESS_PERCENT:
		m_ctrlProgress.SetPos(wParam);
		break;
	case APM_STATUS_VERIFY:
		strdes.LoadString(IDS_STR_VERIFY);
		m_lblupdatesdes.SetWindowText(strdes);
		break;
	case APM_STATUS_DOWNLOADING:
		//m_ctrlProgress.SetPos(wParam);
		strdes.LoadString(IDS_START_DOWNLOAD);
		m_lblupdatesdes.SetWindowText(strdes);
		break;
	case APM_STATUS_INSTALL_PATCH:
		m_ctrlProgress.SetPos(wParam);
		strdes.LoadString(IDS_BEGIN_INSTALL);
		m_lblupdatesdes.SetWindowText(strdes);
		break;
	case APM_STATUS_INSTALL_START:
		m_ctrlProgress.SetPos(wParam);
		strStep.LoadString(IDS_BEGIN_INSTALL);
		m_lblupdatesdes.SetWindowText(strStep);
		//cxl
		pwnd = pSheet->GetDlgItem(IDCANCEL);
		if (pwnd)
		{
			pwnd->EnableWindow(FALSE);
		}
		//
		break;
	case APM_STATUS_BACKEND_MESSAGE:
		wstrrespon = (WCHAR*)wParam;
		strStep.Format(_T("%s"), wstrrespon.c_str());
		//strStep = (WCHAR*)wParam;
		m_ctrlProgress.SetPos(100);
		pSheet->SetWizardButtons(PSWIZB_FINISH);
		pwnd = pSheet->GetDlgItem(IDCANCEL);
		if (pwnd)
		{
			pwnd->EnableWindow(FALSE);
		}
		strStep.LoadString(IDS_NO_UPDATE);
		//strStaUpdate.LoadString(IDS_APM_STEP_FINISH);
		//m_tbtital.SetWindowText(strStaUpdate);
		m_tbdes.SetWindowText(strStep);
		strStep.LoadString(IDS_UPDATE_PROCESS);
		m_lblupdatesdes.SetWindowText(strStep);

		break;
	case APM_STATUS_PATCHVERIFY:
		m_ctrlProgress.SetPos(70);
		strStep.LoadString(IDS_APM_VERIFY);
		m_lblupdatesdes.SetWindowText(strStep);
		break;
	}
	return 0;
}

BOOL CUpdateCheckPage::Validate()
{
	CString strError;

	UpdateData(TRUE);

	if (m_apmSettingModel.m_nServerType == APM_SERVERTYPE_CA)
	{
		if (m_apmSettingModel.m_bProxy)
		{
			if (ValidateServerName(m_apmSettingModel.m_proxyServer.m_strServer) == -1)
			{
				strError.LoadString(IDS_ERROR_PROXY_SERVER_EMPTY);			
				MessageBox(strError, m_strCaption, MB_ICONERROR);
				return FALSE;
			}

			if (!ValidatePort(m_apmSettingModel.m_proxyServer.m_strPort)){
				strError.LoadString(IDS_ERROR_PROXY_PORT_EMPTY);
				MessageBox(strError, m_strCaption, MB_ICONERROR);
				return FALSE;
			}

			if (m_apmSettingModel.m_bProxyAuth)
			{
				if (m_apmSettingModel.m_strProxyUsername.IsEmpty())
				{
					strError.LoadString(IDS_ERROR_PROXY_USERNAME_EMPTY);
					MessageBoxW(strError, m_strCaption, MB_ICONERROR);
					return FALSE;
				}
			}
		}
	}
	else
	{
		if (ValidateServerName(m_apmSettingModel.m_stagingServer.m_strServer) == -1)
		{
			strError.LoadString(IDS_ERROR_STAGING_SERVER_EMPTY);
			MessageBox(strError, m_strCaption, MB_ICONERROR);
			return FALSE;
		}
		else if (ValidateServerName(m_apmSettingModel.m_stagingServer.m_strServer) == 0)
		{
			strError.LoadString(IDS_ERROR_LOCAL_MACHINE);
			MessageBox(strError, m_strCaption, MB_ICONERROR);
			return FALSE;
		}

		if (!ValidatePort(m_apmSettingModel.m_stagingServer.m_strPort)){
			strError.LoadString(IDS_ERROR_STAGING_PORT_EMPTY);
			MessageBox(strError, m_strCaption, MB_ICONERROR);
			return FALSE;
		}
	}

	return TRUE;
}

const EdgeAPM::APMSetting* CUpdateCheckPage::GetApmSetting()
{
	return &m_apmSetting;
}

EdgeAPM::APMSetting CUpdateCheckPage::ConvertFromModel(const APMSettingModel &model)
{
	APMSetting apmSetting;
	apmSetting.m_bProxy = model.m_bProxy;
	apmSetting.m_bProxyAuth = model.m_bProxyAuth;
	apmSetting.m_nServerType = model.m_nServerType;
	apmSetting.m_proxyServer = ConvertFromModel(model.m_proxyServer);
	apmSetting.m_strProxyPwd = model.m_strProxyPwd;
	apmSetting.m_strProxyUsername = model.m_strProxyUsername;
	if (!model.m_stagingServer.m_strServer.IsEmpty())
	{
		apmSetting.m_vecStaging.push_back(ConvertFromModel(model.m_stagingServer));
	}
	return apmSetting;
}

EdgeAPM::ServerInfo CUpdateCheckPage::ConvertFromModel(const ServerInfoModel &model)
{
	ServerInfo svrInfo;

	svrInfo.m_strServer = model.m_strServer;
	if (model.m_strPort.IsEmpty())
		svrInfo.m_nPort = 0;
	else
		svrInfo.m_nPort = _wtoi(model.m_strPort);
	return svrInfo;
}

DWORD CUpdateCheckPage::ValidateServerName(const CString &strName)
{
	wstring strReg = L"[\\^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?%]+";
	CAtlRegExp<> reUrl;
	REParseError status = REPARSE_ERROR_OK;
	CAtlREMatchContext<> mcUrl;

	if (strName.IsEmpty())
		return -1;
	status = reUrl.Parse(strReg.c_str());
	if (REPARSE_ERROR_OK != status){
		return -1;
	}

	TCHAR szComputerName[MAX_PATH];
	DWORD	dwSize = sizeof(szComputerName);
	memset(szComputerName, 0, sizeof(szComputerName));
	if (!::GetComputerName(szComputerName, &dwSize))
	{
		WriteLog(_T("Can not get local machine name"));
	}
	if (strName.CompareNoCase(szComputerName) == 0)
	{
		return 0;
	}
	if (!reUrl.Match(strName, &mcUrl))
	{
		return 1;
	}
	
	return -1;
}

BOOL CUpdateCheckPage::ValidatePort(const CString &strPort)
{
	wstring port = strPort;
	int nPort;
	if (String2Int(port, nPort))
		return FALSE;

	if (nPort > 0 && nPort <= 65535)
		return TRUE;
	else
		return FALSE;
}