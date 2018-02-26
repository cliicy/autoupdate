// APMUtilityDlg.cpp : implementation file
//

#include "stdafx.h"
#include "APMUtility.h"
#include "APMUtilityDlg.h"
#include "Global.h"
#include "APMThread.h"
#include "Utility.h"
#include "Log.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CAPMUtilityDlg dialog




CAPMUtilityDlg::CAPMUtilityDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CAPMUtilityDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	m_hCancelUpdateEvent = CreateEvent( NULL, FALSE, FALSE, NULL);
	m_strCaption.LoadStringW(IDS_productName);
}

void CAPMUtilityDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_PROGRESS1, m_ctrlProgress);		
	DDX_Control(pDX, IDC_LBL_STEP, m_lblStep);
}

BEGIN_MESSAGE_MAP(CAPMUtilityDlg, CDialog)
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	//}}AFX_MSG_MAP		
	ON_MESSAGE(WM_APM_STATUS_CHANGED, &CAPMUtilityDlg::OnAPMStatusChanged)
END_MESSAGE_MAP()


// CAPMUtilityDlg message handlers

BOOL CAPMUtilityDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	// TODO: Add extra initialization here
	m_ctrlProgress.SetRange(0, 100);

	return TRUE;  // return TRUE  unless you set the focus to a control
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CAPMUtilityDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CAPMUtilityDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}


void CAPMUtilityDlg::UpdateNow(const EdgeAPM::APMSetting *pAPMSetting)
{	
	CString strStep;
	m_apmSetting = *pAPMSetting;
	m_ctrlProgress.SetPos(5);
	strStep.LoadString(IDS_APM_STEP_DOWNLOADING);
	m_lblStep.SetWindowText( strStep );
	m_hUpdateThread = CreateThread(0, 0, RunApm, this, 0, 0);			
	CloseHandle(m_hUpdateThread);
	m_nStep = APM_STATUS_BEGIN;			
}

afx_msg LRESULT CAPMUtilityDlg::OnAPMStatusChanged(WPARAM wParam, LPARAM lParam)
{
	CString strStep;
	CString strError;
	DWORD dwRet = 0;
	CAPMUtilityApp *pApp = (CAPMUtilityApp*)::AfxGetApp();
	CString strCaption;
	CString strText;

	switch (lParam)
	{
	case APM_STATUS_DOWNLOAD_FINISHED :
		m_nStep = lParam;
		m_ctrlProgress.SetPos(50);				
		strStep.LoadString(IDS_APM_STEP_INSTALLING);
		m_lblStep.SetWindowText( strStep );
		break;
	case APM_STATUS_INSTALL_FINISHED:
		m_nStep = lParam;
		m_ctrlProgress.SetPos(100);	
		strStep.LoadString( IDS_APM_STEP_FINISH );
		m_lblStep.SetWindowText( strStep );
		if( (BOOL)wParam || ((CAPMUtilityApp*)AfxGetApp())->IsRebootRequired() ){	
			strCaption.LoadStringW(IDS_productName);
			strText.LoadStringW(IDS_REBOOT_SYSTEM_ON_DEMAND);
			if( IDYES == MessageBoxW(strText, strCaption, MB_YESNO | MB_ICONINFORMATION))
				RebootSystem(true);		
		}else{
			strText.LoadStringW(IDS_UPDATE_FINISH);		
			MessageBoxW(strText, m_strCaption, MB_ICONINFORMATION | MB_OK);
		}		
		DestroyParentDlg();
		break;
	case APM_STATUS_ERROR:
		m_hUpdateThread = INVALID_HANDLE_VALUE;
		strError.LoadString( wParam );		
		MessageBoxW(strError, m_strCaption, MB_ICONERROR);		
		DestroySelf();		
		break;
	case APM_STATUS_PROCESS_PERCENT:
		m_ctrlProgress.SetPos( wParam );
		if( wParam == 100 ){
			strStep.LoadString( IDS_APM_STEP_FINISH );
			m_lblStep.SetWindowText( strStep );
		}
		break;
	case APM_STATUS_BACKEND_MESSAGE:
		WCHAR *strMsg = (WCHAR*)wParam;
		MessageBoxW(strMsg, m_strCaption, MB_ICONINFORMATION | MB_OK);
		break;
	}
	return 0;
}

HANDLE CAPMUtilityDlg::GetCancelUpdateEvent()
{
	return m_hCancelUpdateEvent;
}

void CAPMUtilityDlg::PostNcDestroy()
{ 		
	CDialog::PostNcDestroy();	
	delete this; 
}

void CAPMUtilityDlg::DestroyParentDlg()
{
	CWnd*pDlg = (CWnd*)GetParent();	
	pDlg->DestroyWindow();	
}

void CAPMUtilityDlg::DestroySelf()
{
	CWnd*pDlg = (CWnd*)GetParent();	
	pDlg->ShowWindow(SW_SHOW);
	DestroyWindow();
}

const EdgeAPM::APMSetting* CAPMUtilityDlg::GetApmSetting()
{
	return &m_apmSetting;
}
