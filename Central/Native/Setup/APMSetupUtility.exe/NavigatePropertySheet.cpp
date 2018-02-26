// CNavigatePropertySheet.cpp : implementation file
//

#include "stdafx.h"
#include "NavigatePropertySheet.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

#if (_WIN32_IE >= 0x0400)
#define MY_WIZARD_STYLE            0x00002000
#endif


#define ID_WIZTOPLINE		ID_WIZFINISH + 2
#define ID_WIZBOTTOMLINE	ID_WIZFINISH + 1

#define DEFAULT_NAVIGATE_WIDTH		210
#define DEFAULT_SHEET_WIDTH			800
#define DEFAULT_SHEET_HEIGHT		600
#define DEFAULT_SHEET_TITLE_HEIGHT	60

#define PAGE_SPACE		14
#define MESSAGE_SPACE	28

/////////////////////////////////////////////////////////////////////////////
// CNavigatePropertySheet

IMPLEMENT_DYNAMIC(CNavigatePropertySheet, CNavigatePropertySheet_Base)

CNavigatePropertySheet::CNavigatePropertySheet()
{
	Init();
}


CNavigatePropertySheet::CNavigatePropertySheet(UINT nIDCaption, CWnd* pParentWnd,
						 UINT iSelectPage, HBITMAP hbmWatermark, HPALETTE hpalWatermark,
						 HBITMAP hbmHeader)
						 : CNavigatePropertySheet_Base(nIDCaption, pParentWnd, iSelectPage,
						 hbmWatermark, hpalWatermark, hbmHeader)
{
	Init();
}


CNavigatePropertySheet::CNavigatePropertySheet(LPCTSTR pszCaption, CWnd* pParentWnd,
						 UINT iSelectPage, HBITMAP hbmWatermark, HPALETTE hpalWatermark,
						 HBITMAP hbmHeader)
						 : CNavigatePropertySheet_Base(pszCaption, pParentWnd, iSelectPage,
						 hbmWatermark, hpalWatermark, hbmHeader)						 
{
	Init();
}


CNavigatePropertySheet::~CNavigatePropertySheet()
{
}



BEGIN_MESSAGE_MAP(CNavigatePropertySheet, CNavigatePropertySheet_Base)
	//{{AFX_MSG_MAP(CNavigatePropertySheet)
	ON_WM_CREATE()
	ON_WM_SIZE()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CNavigatePropertySheet message handlers

void CNavigatePropertySheet::Init()
{
	m_bInited = FALSE;

	m_psh.dwFlags |= MY_WIZARD_STYLE;
	SetWizardMode();

	m_blShowMessagePanel = TRUE;	
	m_blShowNavigationBar = TRUE;
	//cxl
	//m_nWidth = DEFAULT_NAVIGATE_WIDTH;
	m_nWidth = 0;
	//
	m_rectPage = CRect(m_nWidth+PAGE_SPACE, DEFAULT_SHEET_TITLE_HEIGHT, DEFAULT_SHEET_WIDTH-DEFAULT_NAVIGATE_WIDTH,500);
}


int CNavigatePropertySheet::OnCreate(LPCREATESTRUCT lpCreateStruct) 
{
	this->EnableStackedTabs(FALSE);

	if (CPropertySheet::OnCreate(lpCreateStruct) == -1)
		return -1;
	
	// Increase width of the property sheet
	CRect rectSheet;
	GetWindowRect(rectSheet);
	ScreenToClient(rectSheet);
	rectSheet.right += m_nWidth;
	SetWindowPos(NULL, -1, -1, rectSheet.Width(), rectSheet.Height(), SWP_NOZORDER|SWP_NOMOVE);
	
	CRect rect(rectSheet);
	rect.right = m_nWidth;
	
	m_ctrNavigate.Create(CNavationCtrl::IDD, this);

	m_ctrMessagePanel.Init(this, rect);

	return 0;
}


BOOL CNavigatePropertySheet::OnInitDialog()
{
	CNavigatePropertySheet_Base::OnInitDialog();

	GetWindowRect(m_rectSheet);

	CRect rectSheet;

	GetClientRect(rectSheet);
	m_rectPageDefault = rectSheet;

	CWnd* pWndLine = GetDlgItem(ID_WIZBOTTOMLINE);
	if (pWndLine)
	{
		CRect rectLine;
		pWndLine->GetWindowRect(rectLine);
		ScreenToClient(rectLine);
		m_rectPageDefault.bottom = rectLine.top;
	}
	else
		m_rectPageDefault.bottom -= 40;

	CWnd *pCancel = GetDlgItem(IDCANCEL);
	CWnd *pNext = GetDlgItem(ID_WIZNEXT);
	CWnd *pBack = GetDlgItem(ID_WIZBACK);
	CWnd *pFinish = GetDlgItem(ID_WIZFINISH);

	if (pCancel && pNext && pBack && pFinish)
	{
		int nWidthAdd = 40;
		int nSpace = 10;

		CRect rcCancel, rcNext, rcBack;
		pCancel->GetWindowRect(rcCancel);
		ScreenToClient(rcCancel);

		pNext->GetWindowRect(rcNext);
		ScreenToClient(rcNext);

		pBack->GetWindowRect(rcBack);
		ScreenToClient(rcBack);

		rcCancel.left -= nWidthAdd;
		pCancel->MoveWindow(rcCancel, FALSE);

		int nWidth = rcCancel.Width();

		rcNext.right = rcCancel.left - nSpace;
		rcNext.left = rcNext.right - nWidth;
		pNext->MoveWindow(rcNext, FALSE);
		pFinish->MoveWindow(rcNext, FALSE);

		rcBack.right = rcNext.left - nSpace;
		rcBack.left = rcBack.right - nWidth;
		pBack->MoveWindow(rcBack, FALSE);

	}

	MoveChildWindows(m_nWidth, 0);

	m_ctrMessagePanel.SetBackgroundColor(RGB(255, 255, 225));

	m_ctrMessagePanel.SetFont(GetFont());

	m_ctrMessagePanel.m_ctrButton.SetFont(GetFont());

	SetNavigationBarWidth(m_nWidth);
	
	if (!m_blShowNavigationBar)
	{
		HideNavigationBar();
	}

	HideMessagePanel();

	//Hide the Tab Control
	CWnd *pWnd = GetTabControl();
	CRect rect;
	pWnd->GetWindowRect(rect);
	rect.top += 200;
	rect.bottom = rect.top;
	rect.right = rect.left;
	pWnd->MoveWindow(rect);

	pWnd->ShowWindow(SW_HIDE);

	m_bInited = TRUE;

	return TRUE;
}


void CNavigatePropertySheet::MoveChildWindows(int nDx, int nDy)
{
	CWnd *pWnd = GetWindow(GW_CHILD);
	while (pWnd)
	{
		CRect rect;
		pWnd->GetWindowRect(rect);
		rect.OffsetRect(nDx, nDy);
		ScreenToClient(rect);

		pWnd->MoveWindow(rect);

		pWnd = pWnd->GetNextWindow();
	}
}


void CNavigatePropertySheet::AddPage(CNavigatePropertyPage *pPage)
{
	CNavigatePropertySheet_Base::AddPage(pPage);
}


void CNavigatePropertySheet::SetNavigationBarWidth(int nWidth)
{
	MoveChildWindows(nWidth-m_nWidth, 0);

	m_nWidth = nWidth;
	m_rectPage.left = m_nWidth + PAGE_SPACE;
	//cxl
	m_rectPage.left = PAGE_SPACE;
	//
	CRect rect, rectSheet, rcNavigate, rectMessage;

	GetClientRect(rectSheet);

	m_rectPage.right = rectSheet.right - PAGE_SPACE;

	CWnd* pWndLine = GetDlgItem(ID_WIZTOPLINE);
	if (pWndLine)
	{
		pWndLine->GetWindowRect(rect);
		ScreenToClient(rect);
		rect.left = rectSheet.left;
		rect.right = rectSheet.right;
		pWndLine->SetWindowPos(NULL, rect.left, rect.top, rect.Width(), rect.Height(), SWP_NOZORDER);

		m_rectPage.top = rect.bottom + MESSAGE_SPACE;

		rcNavigate.top = rect.bottom;

		rectMessage.top = rect.bottom;
		rectMessage.bottom  = rectMessage.top + MESSAGE_SPACE - 1;
	}

	pWndLine = GetDlgItem(ID_WIZBOTTOMLINE);
	if (pWndLine)
	{
		pWndLine->GetWindowRect(rect);
		ScreenToClient(rect);
		rect.left = rectSheet.left;
		rect.right = rectSheet.right;
		pWndLine->SetWindowPos(NULL, rect.left, rect.top, rect.Width(), rect.Height(), SWP_NOZORDER);

		m_rectPage.bottom = rect.top - PAGE_SPACE;

		rcNavigate.bottom = rect.top;
	}

	//Set the position of Message panel
	if (m_ctrMessagePanel.GetSafeHwnd())
	{
		rectMessage.left = rectSheet.left;
		rectMessage.right  = rectSheet.right;

		m_ctrMessagePanel.SetWindowPos(NULL, rectMessage.left, rectMessage.top, rectMessage.Width(), rectMessage.Height(), SWP_NOZORDER);
	}

	//Set the position of Navigate panel
	if (m_ctrNavigate.GetSafeHwnd())
	{
		m_ctrNavigate.GetWindowRect(rect);
		ScreenToClient(rect);

		rcNavigate.left = rectSheet.left;
		rcNavigate.right = m_nWidth;

		if (m_blShowMessagePanel)
		{
			rcNavigate.top = rectMessage.bottom;
		}
		else
		{
			rcNavigate.top = rectMessage.top;
		}

		m_ctrNavigate.SetWindowPos(NULL, rcNavigate.left, rcNavigate.top, rcNavigate.Width(), rcNavigate.Height(), SWP_NOZORDER);
		m_ctrNavigate.RePositionControl();
	}

	//Set the position of Page
	CPropertyPage* pPage = GetActivePage();
	if (pPage)
	{
		if ((pPage->m_psp.dwFlags & PSP_HIDEHEADER) == 0)
			pPage->MoveWindow(m_rectPage);
		else
			pPage->MoveWindow(m_rectPageDefault);
	}
}


void CNavigatePropertySheet::HideNavigationBar()
{
	m_blShowNavigationBar = FALSE;
	if (GetSafeHwnd())
	{
		if (m_ctrNavigate.GetSafeHwnd())
		{
			m_ctrNavigate.ShowWindow(SW_HIDE);
		}
	}
}


void CNavigatePropertySheet::ShowNavigationBar()
{
	m_blShowNavigationBar = TRUE;

	if (GetSafeHwnd())
	{
		if (m_ctrNavigate.GetSafeHwnd())
		{
			m_ctrNavigate.ShowWindow(SW_SHOW);
		}
	}
}

void CNavigatePropertySheet::SetCurrentStatusByHeaderTitleID(UINT nHeaderTitleID)
{
	m_ctrNavigate.SetCurrentStatusByHeaderTitleID(nHeaderTitleID);
}

void CNavigatePropertySheet::SetCurrentStatus(DWORD dwID)
{
	m_ctrNavigate.SetCurrentStatus(dwID);
}

void CNavigatePropertySheet::EnableStatusByHeaderTitleID(UINT nHeaderTitleID, BOOL bEnalbe, BOOL bUpdate)
{
	m_ctrNavigate.EnableStatusByHeaderTitleID(nHeaderTitleID, bEnalbe, bUpdate);
}


void CNavigatePropertySheet::EnableStatus(DWORD dwID, BOOL bEnalbe, BOOL bUpdate)
{
	m_ctrNavigate.EnableStatus(dwID, bEnalbe, bUpdate);
}


void CNavigatePropertySheet::EnableStatus(DWORD dwIDBegin, DWORD dwIDEnd, BOOL bEnalbe, BOOL bUpdate)
{
	m_ctrNavigate.EnableStatus(dwIDBegin, dwIDEnd, bEnalbe, bUpdate);
}


ARRAY_STATUS* CNavigatePropertySheet::GetAllStatus()
{
	return m_ctrNavigate.GetAllStatus();
}


void CNavigatePropertySheet::SetAllStatus(ARRAY_STATUS* status)
{
	m_ctrNavigate.SetAllStatus(status);
}


void CNavigatePropertySheet::AddMessage(CString strMsg, MESSAGE_TYPE type)
{
	m_ctrMessagePanel.AddMessage(strMsg, type);
	ShowMessagePanel();
}


void CNavigatePropertySheet::RemoveAllMessage()
{
	m_ctrMessagePanel.RemoveAllMessage();
	HideMessagePanel();
}


void CNavigatePropertySheet::HideMessagePanel()
{
	if (m_blShowMessagePanel)
	{
		m_ctrMessagePanel.ShowWindow(SW_HIDE);
		m_blShowMessagePanel = FALSE;

		CWnd* pWndBomLine = GetDlgItem(ID_WIZBOTTOMLINE);
		ASSERT(pWndBomLine != NULL);

		CWnd* pWndTopLine = GetDlgItem(ID_WIZTOPLINE);
		ASSERT(pWndTopLine != NULL);

		CRect rectTopLine;
		pWndTopLine->GetWindowRect(rectTopLine);
		ScreenToClient(rectTopLine);

		CRect rectBomLine;
		pWndBomLine->GetWindowRect(rectBomLine);
		ScreenToClient(rectBomLine);

		CRect reMsg, rcNav;
		m_ctrMessagePanel.GetWindowRect(reMsg);
		ScreenToClient(reMsg);
		m_ctrNavigate.GetWindowRect(rcNav);
		ScreenToClient(rcNav);

		rcNav.top = rectTopLine.bottom;
		rcNav.bottom = rectBomLine.top;

		m_ctrNavigate.SetWindowPos(NULL, rcNav.left, rcNav.top, rcNav.Width(), rcNav.Height(), SWP_NOZORDER);
		m_ctrNavigate.RePositionControl();
	}
}


void CNavigatePropertySheet::ShowMessagePanel()
{
	if (!m_blShowMessagePanel)
	{
		m_ctrMessagePanel.ShowWindow(SW_SHOW);
		m_blShowMessagePanel = TRUE;

		CWnd* pWndBomLine = GetDlgItem(ID_WIZBOTTOMLINE);
		ASSERT(pWndBomLine != NULL);

		CRect rectBomLine;
		pWndBomLine->GetWindowRect(rectBomLine);
		ScreenToClient(rectBomLine);

		CRect reMsg, rcNav;
		m_ctrMessagePanel.GetWindowRect(reMsg);
		ScreenToClient(reMsg);
		m_ctrNavigate.GetWindowRect(rcNav);
		ScreenToClient(rcNav);

		rcNav.top = reMsg.bottom;
		rcNav.bottom = rectBomLine.top;

		m_ctrNavigate.SetWindowPos(NULL, rcNav.left, rcNav.top, rcNav.Width(), rcNav.Height(), SWP_NOZORDER);
		m_ctrNavigate.RePositionControl();
	}
}


void CNavigatePropertySheet::GetStatusFromFile(CString strFileName)
{
	m_ctrNavigate.GetStatusFromFile(strFileName);
}


void CNavigatePropertySheet::SetStatusToFile(CString strFileName)
{
	m_ctrNavigate.SetStatusToFile(strFileName);
}


void CNavigatePropertySheet::OnSize(UINT nType, int cx, int cy) 
{
	CNavigatePropertySheet_Base::OnSize(nType, cx, cy);

	if (m_bInited)
	{
		CRect rcCurrent;
		GetWindowRect(rcCurrent);

		SetWindowPos(NULL, rcCurrent.left, rcCurrent.top, m_rectSheet.Width(), m_rectSheet.Height(), SWP_NOZORDER|SWP_NOMOVE);
	}
}