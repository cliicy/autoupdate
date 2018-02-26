// StaticEx.cpp : implementation file
//

#include "stdafx.h"
#include "StaticEx.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CStaticEx

CStaticEx::CStaticEx()
{
	m_crText = GetSysColor(COLOR_WINDOWTEXT);
	m_hBrush = ::CreateSolidBrush(GetSysColor(COLOR_3DFACE));

	::GetObject((HFONT)GetStockObject(DEFAULT_GUI_FONT), sizeof(m_lf), &m_lf);

	m_font.CreateFontIndirect(&m_lf);
	m_bTimer = FALSE;
	m_bState = FALSE;
	m_bLink = TRUE;
	m_hCursor = NULL;
	m_Type = None;

	m_hwndBrush = ::CreateSolidBrush(GetSysColor(COLOR_3DFACE));
}


CStaticEx::~CStaticEx()
{
	m_font.DeleteObject();
	::DeleteObject(m_hBrush);
}


CStaticEx& CStaticEx::SetText(const CString& strText)
{
	SetWindowText(strText);
	return *this;
}


CStaticEx& CStaticEx::SetTextColor(COLORREF crText)
{
	m_crText = crText;
	RedrawWindow();
	return *this;
}


CStaticEx& CStaticEx::SetFontBold(BOOL bBold)
{	
	m_lf.lfWeight = bBold ? FW_BOLD : FW_NORMAL;
	ReconstructFont();
	RedrawWindow();
	return *this;
}


CStaticEx& CStaticEx::SetFontUnderline(BOOL bSet)
{	
	m_lf.lfUnderline = (BYTE)bSet;
	ReconstructFont();
	RedrawWindow();
	return *this;
}


CStaticEx& CStaticEx::SetFontItalic(BOOL bSet)
{
	m_lf.lfItalic = (BYTE)bSet;
	ReconstructFont();
	RedrawWindow();
	return *this;	
}


CStaticEx& CStaticEx::SetSunken(BOOL bSet)
{
	if (!bSet)
		ModifyStyleEx(WS_EX_STATICEDGE, 0, SWP_DRAWFRAME);
	else
		ModifyStyleEx(0, WS_EX_STATICEDGE, SWP_DRAWFRAME);

	return *this;	
}


CStaticEx& CStaticEx::SetBorder(BOOL bSet)
{
	if (!bSet)
		ModifyStyle(WS_BORDER, 0, SWP_DRAWFRAME);
	else
		ModifyStyle(0, WS_BORDER, SWP_DRAWFRAME);

	return *this;	
}


CStaticEx& CStaticEx::SetFontSize(int nSize)
{
	nSize *= -1;
	m_lf.lfHeight = nSize;
	ReconstructFont();
	RedrawWindow();
	return *this;
}


CStaticEx& CStaticEx::SetBkColor(COLORREF crBkgnd)
{
	if (m_hBrush)
		::DeleteObject(m_hBrush);

	m_hBrush = ::CreateSolidBrush(crBkgnd);
	return *this;
}


CStaticEx& CStaticEx::SetFontName(const CString& strFont)
{	
	_tcscpy_s(m_lf.lfFaceName, LF_FACESIZE, strFont);
	ReconstructFont();
	RedrawWindow();
	return *this;
}


void CStaticEx::ReconstructFont()
{
	m_font.DeleteObject();
	BOOL bCreated = m_font.CreateFontIndirect(&m_lf);

	ASSERT(bCreated);
}


CStaticEx& CStaticEx::FlashText(BOOL bActivate)
{
	if (m_bTimer)
	{
		SetWindowText(m_strText);
		KillTimer(1);
	}

	if (bActivate)
	{
		GetWindowText(m_strText);
		m_bState = FALSE;

		m_bTimer = TRUE;
		SetTimer(1,500,NULL);
		m_Type = Text;
	}

	return *this;
}


CStaticEx& CStaticEx::FlashBackground(BOOL bActivate)
{

	if (m_bTimer)
		KillTimer(1);

	if (bActivate)
	{
		m_bState = FALSE;

		m_bTimer = TRUE;
		SetTimer(1,500,NULL);

		m_Type = Background;
	}

	return *this;
}


CStaticEx& CStaticEx::SetLink(BOOL bLink)
{
	m_bLink = bLink;

	if (bLink)
		ModifyStyle(0, SS_NOTIFY);
	else
		ModifyStyle(SS_NOTIFY, 0);

	return *this;
}


CStaticEx& CStaticEx::SetLinkCursor(HCURSOR hCursor)
{
	m_hCursor = hCursor;
	return *this;
}


BEGIN_MESSAGE_MAP(CStaticEx, CStatic)
	//{{AFX_MSG_MAP(CStaticEx)
	//ON_WM_TIMER()
	ON_WM_CTLCOLOR_REFLECT()
	ON_WM_LBUTTONDOWN()
	ON_WM_SETCURSOR()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CStaticEx message handlers

/*void CStaticEx::OnTimer(UINT nIDEvent) 
{
	m_bState = !m_bState;

	switch (m_Type)
	{
	case Text:
		if (m_bState)
			SetWindowText(_T(""));
		else
			SetWindowText(m_strText);
		break;

	case Background:
		InvalidateRect(NULL, FALSE);
		UpdateWindow();
		break;
	}

	CStatic::OnTimer(nIDEvent);
}*/


HBRUSH CStaticEx::CtlColor(CDC* pDC, UINT nCtlColor) 
{
	// TODO: Return a non-NULL brush if the parent's handler should not be called
	if (CTLCOLOR_STATIC == nCtlColor)
	{
		pDC->SelectObject(&m_font);
		pDC->SetTextColor(m_crText);
		pDC->SetBkMode(TRANSPARENT);
	}


	if (m_Type == Background)
	{
		if (!m_bState)
			return m_hwndBrush;
	}

	return m_hBrush;
}


void CStaticEx::OnLButtonDown(UINT nFlags, CPoint point) 
{
	// TODO: Add your message handler code here and/or call default
	//CString strLink;
	//strLink = _T("http://www.ca.com");
	//ShellExecute(NULL, _T("open"), strLink, NULL, NULL, SW_SHOWNORMAL);

	CStatic::OnLButtonDown(nFlags, point);
}


BOOL CStaticEx::OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message) 
{
	// TODO: Add your message handler code here and/or call default
	if (m_hCursor)
	{
		::SetCursor(m_hCursor);
		return TRUE;
	}	
	return CStatic::OnSetCursor(pWnd, nHitTest, message);
}
