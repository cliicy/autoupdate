// SetupLicenseDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Uninstall.h"
#include "SetupComponentPage.h"
#include "SetupSheet.h"

#ifndef UNICODE
#include <io.h>
#endif

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////////
#define PROCESS_TIMER_ID 200
#define TIMER_CYCLE		100

enum COLUMN_INDEX{COL_NAME = 0, COL_STATUE = 1};

enum{ICON_PENDING = 0, ICON_WORKING, ICON_FINISH, ICON_FAILED, ICON_REBOOT};

//////////////////////////////////////////////////////////////////////////

#define WM_CHANGE_FONT WM_USER+1000

/////////////////////////////////////////////////////////////////////////////
// CSetupComponentPage property page

IMPLEMENT_DYNCREATE(CSetupComponentPage, CSetupPage)

CSetupComponentPage::CSetupComponentPage() : CSetupPage(CSetupComponentPage::IDD, 0, 0, 0)
{
	m_nNagivageTitleID = IDS_STRING_HEAD;
	m_strPending = _T("");
	m_strWorking = _T("");
	m_strCompleted = _T("");
	m_strFailed = _T("");
	m_strWorkingMsg = _T("");
	m_strFinishMsg = _T("");
	m_strFailedMsg = _T("");
}

CSetupComponentPage::~CSetupComponentPage()
{
}

void CSetupComponentPage::DoDataExchange(CDataExchange* pDX)
{
	CSetupPage::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSetupComponentPage)
	DDX_Control(pDX, IDC_LIST_PREREQUISITES, m_ctrList);
	DDX_Control(pDX, IDC_PROGRESS_INSTALLPRE, m_ctrProgress);
	DDX_Control(pDX, IDC_STATIC_REMOVECOMPONENT, m_ctrRCStatic);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CSetupComponentPage, CSetupPage)
	//{{AFX_MSG_MAP(CSetupComponentPage)
	ON_BN_CLICKED(IDC_CHECK_CONFIRM_REMOVE, OnBnClickedCheck)
	ON_WM_TIMER()
	ON_MESSAGE(WM_CHANGEITEMSTATUS, OnChangeStatus)
	ON_MESSAGE(WM_INSTALLFINISH,	OnFinish)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSetupComponentPage message handlers

BOOL CSetupComponentPage::OnInitDialog() 
{
	CSetupPage::OnInitDialog();	

	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheetEx, pSheet);

	m_strPending.LoadString(IDS_STRING_PENDING);
	m_strWorking.LoadString(IDS_STRING_INSTALLING);
	m_strCompleted.LoadString(IDS_STRING_REMOVED);
	m_strFailed.LoadString(IDS_STRING_FAILED);
	m_strWorkingMsg.LoadString(IDS_STRING_MSG_INSTALLING);
	m_strFinishMsg.LoadString(IDS_STRING_MSG_INSTALLFINISH);
	m_strFailedMsg.LoadString(IDS_STRING_MSG_FAILED);

	m_ctrProgress.ShowWindow(SW_HIDE);

	m_ctrRCStatic.SetFontBold(TRUE);

	InitListCtrl();

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}


BOOL CSetupComponentPage::OnSetActive() 
{
	BOOL blRet = CSetupPage::OnSetActive();

	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	ListProducts();

	CString strText;

	CWnd *pWnd;

	pWnd = pSheet->GetDlgItem(ID_WIZNEXT);
	if (pWnd)
	{
		strText.LoadString(IDS_STRING_WIZ_INSTALL);

		if (strText.IsEmpty())
		{
			strText = _T("&Uninstall");
		}

		pWnd->SetWindowText(strText);

		CButton *pCheck = (CButton *)GetDlgItem(IDC_CHECK_CONFIRM_REMOVE);
		if (pCheck)
		{
			pWnd->EnableWindow(pCheck->GetCheck());

			pCheck->ShowWindow(SW_SHOW);
		}
		else
		{
			pWnd->EnableWindow(TRUE);
		}
	}

	pWnd = GetDlgItem(IDC_STATIC_MSG);
	if (pWnd)
	{
		pWnd->SetWindowText(_T(""));
		pWnd->ShowWindow(SW_HIDE);
	}

	return blRet;
}

LRESULT CSetupComponentPage::OnWizardBack()
{
	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	CWnd *pWnd;

	pWnd = pSheet->GetDlgItem(ID_WIZNEXT);
	if (pWnd)
	{
		CString strText;

		strText.LoadString(IDS_STRING_WIZ_NEXT);

		if (strText.IsEmpty())
		{
			strText = _T("&Next");
		}

		pWnd->SetWindowText(strText);

		pWnd->EnableWindow(TRUE);
	}

	return CSetupPage::OnWizardBack();
}

LRESULT CSetupComponentPage::OnWizardNext()
{
	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	EnableSheetButton(IDCANCEL, FALSE);

	EnableSheetButton(ID_WIZNEXT, FALSE);

	EnableSheetButton(ID_WIZBACK, FALSE);

	m_ctrProgress.ShowWindow(SW_SHOW);

	CWnd *pWnd = GetDlgItem(IDC_STATIC_MSG);
	if (pWnd)
	{
		pWnd->ShowWindow(SW_SHOW);
	}

	CStatic *pRemove = (CStatic *)GetDlgItem(IDC_REMOVE);
	if (pRemove)
	{
		CString strRemove;
		strRemove.LoadString(IDS_STRING_TOPREMOVING);
		pRemove->SetWindowText(strRemove);
	}

	UninstallProducts();

	return -1;
}


LRESULT CSetupComponentPage::OnChangeStatus(WPARAM wParam, LPARAM lParam)
{
	TRACE(_T("OnChangeStatus Parameters: wParam=%d lParam=%d\n"), wParam, lParam);

	int nArrayIndex = (int)wParam;
	int nIndex = 0;

	int nSize = m_ctrList.GetItemCount();

	if (nArrayIndex >= 0 && nArrayIndex < theApp.m_aryProducts.GetSize())
	{
		CString strProdName = theApp.m_aryProducts[nArrayIndex].m_strProductName;

		int nIndex = GetRowByProductName(strProdName);
		if (nIndex >= 0)
		{
			CString strText = GetStatusFromValue((UNINSTALL_STATUS)lParam);
			m_ctrList.SetItemText(nIndex, COL_STATUE, strText);
			int nIcon = GetStatusIcon((UNINSTALL_STATUS)lParam);
			m_ctrList.SetItem(nIndex, 0, LVIF_IMAGE, NULL, nIcon, 0 , 0 , 0 );
		}

		int nStep = 1;
		int nSteps = theApp.m_aryProducts[nArrayIndex].m_nEstInstallTime * 1000 / TIMER_CYCLE * nStep;

		CWnd *pWnd = GetDlgItem(IDC_STATIC_MSG);
		CString strMsg;

		if (M_STATUS_WORKING == lParam)
		{
			m_ctrProgress.SetRange32(1, nSteps);
			m_ctrProgress.SetStep(nStep);
			m_ctrProgress.SetPos(0);

			strMsg.Format(m_strWorkingMsg, theApp.m_aryProducts[nArrayIndex].m_strProductName);
			if (pWnd)
			{
				pWnd->SetWindowText(strMsg);
			}

			SetTimer(PROCESS_TIMER_ID, TIMER_CYCLE, NULL);
		}
		else
		{
			m_ctrProgress.SetPos(nSteps);

			KillTimer(PROCESS_TIMER_ID);

			if (M_STATUS_FAILED == lParam)
			{
				if (pWnd)
				{
					pWnd->SetWindowText(m_strFailedMsg);
					m_ctrList.SetItemURL(nIndex, 1, theApp.m_strLogFile, theApp.m_strLogFile);
				}
			}
		}
	}

	return 0;
}


LRESULT CSetupComponentPage::OnFinish(WPARAM wParam, LPARAM lParam)
{
	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	EnableSheetButton(IDCANCEL, TRUE);

	m_ctrList.EnableWindow(TRUE);

	m_ctrProgress.ShowWindow(SW_HIDE);
	m_ctrProgress.SetPos(0);

	CWnd *pWnd = GetDlgItem(IDC_STATIC_MSG);
	if (pWnd)
		pWnd->ShowWindow(SW_HIDE);


	/* fix issue 13100 - 2013-1-19
	if (theApp.IsAllProductsUninstalled())
	{
		CString strFinishText;
		strFinishText.LoadString(IDS_BTNTEXT_FINISH);
		pSheet->SetWizardButtons(PSWIZB_FINISH);
		pSheet->SetFinishText(strFinishText);
		pSheet->PressButton(PSBTN_FINISH);
	}
	else
	{
		if (theApp.IsNeedReboot())
		{
			CString strTitle, strMsg;

			strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);

			strMsg.LoadString(IDS_MSG_REBOOT);

			if (IDYES == MessageBox(strMsg, strTitle, MB_YESNO|MB_ICONQUESTION))
			{
				theApp.WriteLog(_T("User clicked Yes to reboot OS."));

				theApp.RebootWinnt();
			}
			else
			{
				theApp.WriteLog(_T("User clicked No to cancel the reboot."));
			}
		}

		pSheet->SetActivePage(0);
	}
	*/

	//hide cancel button
	CWnd *pWndCancel = pSheet->GetDlgItem(IDCANCEL);
	if (pWndCancel)
	{
		pWndCancel->ShowWindow(SW_HIDE);
	}


	CStatic *pRemove = (CStatic *)GetDlgItem(IDC_REMOVE);
	if (pRemove)
	{
		CString strRemove;
		strRemove.LoadString(IDS_STRING_REMOVEFINISH);
		pRemove->SetWindowText(strRemove);
	}

	//enable finish button
	CString strFinishText;
	strFinishText.LoadString(IDS_STRING_WIZ_FINISH);
	if (strFinishText.IsEmpty())
	{
		strFinishText = _T("&Finish");
	}
	pSheet->SetWizardButtons(PSWIZB_FINISH);
	pSheet->SetFinishText(strFinishText);

	return 0;
}

int CSetupComponentPage::GetStatusIcon(UNINSTALL_STATUS nStatus)
{
	int nIndex = 0;
	switch (nStatus)
	{
	case M_STATUS_FAILED:
		nIndex = ICON_FAILED;
		break;
	case M_STATUS_WORKING:
		nIndex = ICON_WORKING;
		break;
	case M_STATUS_COMPLETED:
		nIndex = ICON_FINISH;
		break;
	case M_STATUS_PENDING: 
		nIndex = ICON_PENDING;
		break;
	default:
		break;
	}

	return nIndex;
}

CString CSetupComponentPage::GetStatusFromValue(UNINSTALL_STATUS nStatus)
{
	CString strStatus(_T(""));
	switch (nStatus)
	{
	case M_STATUS_FAILED:
		strStatus = m_strFailed;
		break;
	case M_STATUS_WORKING:
		strStatus = m_strWorking;
		break;
	case M_STATUS_COMPLETED:
		strStatus = m_strCompleted;
		break;
	case M_STATUS_PENDING: 
		strStatus = m_strPending;
		break;
	default:
		break;
	}

	return strStatus;
}


void CSetupComponentPage::ListProducts()
{
	m_ctrList.DeleteAllItems();

	CString strStatus;

	int j = 0;
	for (int i=0; i<theApp.m_aryProducts.GetSize(); i++)
	{
		if (theApp.m_aryProducts[i].m_blSelected)
		{
			int nIcon = GetStatusIcon(theApp.m_aryProducts[i].m_nStatus);

			m_ctrList.InsertItem(j, theApp.m_aryProducts[i].m_strProductName, nIcon);

			strStatus = GetStatusFromValue(theApp.m_aryProducts[i].m_nStatus);

			m_ctrList.SetItemText(j, COL_STATUE, strStatus);

			j++;
		}
	}
}

void CSetupComponentPage::UninstallProducts()
{
	theApp.UninstallProducts(GetSafeHwnd(), FALSE);
}


void CSetupComponentPage::OnTimer(UINT_PTR nIDEvent) 
{
	if (PROCESS_TIMER_ID == nIDEvent)
	{
		m_ctrProgress.StepIt();
	}

	CDialog::OnTimer(nIDEvent);
}


void CSetupComponentPage::InitListCtrl()
{
	CString strText;

	//Create Image list
	m_objImageList.Create(16, 16, ILC_COLOR24|ILC_MASK, 5, 5);

	CBitmap bm;
	bm.LoadBitmap(IDB_STATUS_ICONS);
	m_objImageList.Add(&bm, RGB(255, 0, 255));

	m_ctrList.SetImageList(&m_objImageList, LVSIL_SMALL);

	//Insert Columns
	strText.LoadString(IDS_PREREQUISITES_NAME);

	//align the header string to other items string
	CString strAlignText;
	strAlignText.Format(_T("    %s"),strText);
	strText = strAlignText;

	m_ctrList.InsertColumn(0, strText, LVCFMT_LEFT, 405);

	strText.LoadString(IDS_PREREQUISITES_STATUS);
	m_ctrList.InsertColumn(1, strText, LVCFMT_LEFT, 125);

	m_ctrList.SetExtendedStyle(m_ctrList.GetExtendedStyle() /*| LVS_EX_CHECKBOXES | LVS_EX_GRIDLINES*/ | LVS_EX_FULLROWSELECT | LVS_EX_SUBITEMIMAGES);

	//set the font of header string
	CHeaderCtrl* pHeadCtrl = m_ctrList.GetHeaderCtrl();
	if(pHeadCtrl != NULL)
	{
		::GetObject((HFONT)GetStockObject(DEFAULT_GUI_FONT), sizeof(m_lf), &m_lf);

		m_lf.lfWeight = FW_BOLD;
		m_font.CreateFontIndirect(&m_lf);
		pHeadCtrl->SetFont(&m_font);
	}
	//end set font
}


int CSetupComponentPage::GetRowByProductName(const CString &strProdName)
{
	int nSize = m_ctrList.GetItemCount();
	for (int i=0; i<nSize; i++)
	{
		if (m_ctrList.GetItemText(i, 0) == strProdName)
		{
			return i;
		}
	}

	return -1;
}

void CSetupComponentPage::EnableSheetButton(UINT nID, BOOL blEnable)
{
	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	CWnd *pWnd = pSheet->GetDlgItem(nID);
	if (pWnd)
	{
		pWnd->EnableWindow(blEnable);
	}
}

void CSetupComponentPage::OnBnClickedCheck()
{
	CButton *pCheck = (CButton *)GetDlgItem(IDC_CHECK_CONFIRM_REMOVE);
	if (pCheck)
	{
		EnableSheetButton(ID_WIZNEXT, pCheck->GetCheck());
	}
	else
	{
		EnableSheetButton(ID_WIZNEXT, TRUE);
	}
}
