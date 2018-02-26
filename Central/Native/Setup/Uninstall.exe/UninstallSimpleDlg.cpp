
// UninstallSimpleDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Uninstall.h"
#include "UninstallSimpleDlg.h"
#include "afxdialogex.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CUninstallSimpleDlg dialog

#define PROCESS_TIMER_EX_ID 300
#define TIMER_CYCLE		100


CUninstallSimpleDlg::CUninstallSimpleDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CUninstallSimpleDlg::IDD, pParent)
{
	//m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	m_bStart = FALSE;
	m_nCurrentPos = 0;
	m_nProductPos = 0;
	m_bUnintallFailed = FALSE;
}

void CUninstallSimpleDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CUninstallSimpleDlg)
	DDX_Control(pDX, IDC_PROGRESS_REMOVING, m_ctrProgress);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CUninstallSimpleDlg, CDialog)
	ON_WM_PAINT()
	ON_WM_TIMER()
	ON_WM_QUERYDRAGICON()
	ON_WM_SHOWWINDOW()
	ON_MESSAGE(WM_INSTALLFINISH,OnFinish)
	ON_MESSAGE(WM_CHANGEITEMSTATUS, OnChangeStatus)
	ON_MESSAGE(WM_REMOVE_START, OnRemoveStart)
END_MESSAGE_MAP()


// CUninstallSimpleDlg message handlers

BOOL CUninstallSimpleDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	// TODO: Add extra initialization here
	
	CWnd* pWnd = GetDlgItem(IDC_STATIC_REMOVING);
	if (pWnd)
	{
		CString strText;
		strText.Format(IDS_STRING_REMOVING,theApp.m_strProductDisplayName);

		if (!strText.IsEmpty())
		{
			pWnd->SetWindowText(strText);
		}
	}

	return TRUE;  // return TRUE  unless you set the focus to a control
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CUninstallSimpleDlg::OnPaint()
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
HCURSOR CUninstallSimpleDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

void CUninstallSimpleDlg::OnShowWindow(BOOL bShow, UINT nStatus)
{
	CDialog::OnShowWindow(bShow, nStatus);

	//start extract thread
	PostMessage(WM_REMOVE_START, 0, 0);

}

LRESULT CUninstallSimpleDlg::OnFinish(WPARAM wParam, LPARAM lParam)
{
	KillTimer(PROCESS_TIMER_EX_ID);
	m_ctrProgress.SetPos(m_nSteps);
	
	OnOK();

	return 0;
}

void CUninstallSimpleDlg::OnTimer(UINT_PTR nIDEvent) 
{
	if (PROCESS_TIMER_EX_ID == nIDEvent)
	{
		if(m_nCurrentPos < m_nSteps && m_nCurrentPos < m_nProductPos)
		{
			m_ctrProgress.SetPos(m_nCurrentPos);
			m_nCurrentPos++;
		}
	}

	CDialog::OnTimer(nIDEvent);
}

LRESULT CUninstallSimpleDlg::OnRemoveStart(WPARAM wParam, LPARAM lParam)
{
	if(!m_bStart)
	{
		m_bStart = TRUE;

		m_nCurrentPos = 0;
		int nEstInstallTimeTotal = 0;
		for(int i = 0 ;i < theApp.m_aryProducts.GetSize();i++)
		{
			if(theApp.m_aryProducts[i].m_blSelected)
			{
				nEstInstallTimeTotal += theApp.m_aryProducts[i].m_nEstInstallTime;
			}
		}
		
		m_nSteps = nEstInstallTimeTotal * 1000 / TIMER_CYCLE;

		if(m_nSteps <1)
		{
			//set the defualt value
			m_nSteps = 1000;
		}


		m_ctrProgress.SetRange32(1, m_nSteps);
		m_ctrProgress.SetPos(0);

		theApp.UninstallProducts(GetSafeHwnd(), FALSE);
	}

	return 0;
}

LRESULT CUninstallSimpleDlg::OnChangeStatus(WPARAM wParam, LPARAM lParam)
{
	TRACE(_T("OnChangeStatus Parameters: wParam=%d lParam=%d\n"), wParam, lParam);

	int nArrayIndex = (int)wParam;

	if (nArrayIndex >= 0 && nArrayIndex < theApp.m_aryProducts.GetSize())
	{
		
		if (M_STATUS_WORKING == lParam)
		{
			int nSteps = theApp.m_aryProducts[nArrayIndex].m_nEstInstallTime * 1000 / TIMER_CYCLE + m_nProductPos;
			m_nProductPos = nSteps;
			SetTimer(PROCESS_TIMER_EX_ID, TIMER_CYCLE, NULL);
		}
		else
		{
			
			if(m_nCurrentPos < m_nProductPos)
			{
				m_nCurrentPos = m_nProductPos;
				m_ctrProgress.SetPos(m_nCurrentPos);
			}
			
			KillTimer(PROCESS_TIMER_EX_ID);

			//check if the installation failed or not
			if(M_STATUS_FAILED == lParam)
			{
				m_bUnintallFailed = TRUE;
			}
		}

	}

	return 0;
}