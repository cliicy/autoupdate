// StatusCtrl.cpp : implementation file
//

#include "stdafx.h"
#include "StatusCtrl.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CStatusCtrl

CStatusCtrl::CStatusCtrl()
{
}


CStatusCtrl::~CStatusCtrl()
{
}


BEGIN_MESSAGE_MAP(CStatusCtrl, CTreeCtrl)
	//{{AFX_MSG_MAP(CStatusCtrl)
	ON_WM_LBUTTONDOWN()
	ON_WM_RBUTTONDOWN()
	ON_WM_LBUTTONDBLCLK()
	ON_WM_KEYDOWN()
	ON_WM_SETFOCUS()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CStatusCtrl message handlers

void CStatusCtrl::OnLButtonDown(UINT nFlags, CPoint point)
{
}


void CStatusCtrl::OnRButtonDown(UINT nFlags, CPoint point)
{
}


void CStatusCtrl::OnLButtonDblClk(UINT nFlags, CPoint point)
{
}


void CStatusCtrl::OnSetFocus(CWnd* pOldWnd)
{
	if (pOldWnd)
		pOldWnd->SetFocus();
}


void CStatusCtrl::OnKeyDown(UINT/* nChar*/, UINT/* nRepCnt*/, UINT/* nFlags*/)
{
}