// MenuButton.cpp : implementation file

#include "stdafx.h"
#include "MenuButton.h"
#include "CAMenu.h"
#include "resource.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#ifndef GRAY_AREA_LENGTH
#define GRAY_AREA_LENGTH 0x00000019
#endif
/////////////////////////////////////////////////////////////////////////////

// CMenuButton


IMPLEMENT_DYNAMIC(CMenuButton, CButton)


CMenuButton::CMenuButton()
{
	m_bMouseOverButton = FALSE;

	m_cBackground = RGB(255, 255, 225);

	m_MessageTypeImages.Create(IDB_MESSAGE_ICONS, 16, 0, RGB(236, 233, 216));

	if (!::ImageList_GetIconSize(m_MessageTypeImages, &m_nICONcx, &m_nICONcy))
	{
		m_nICONcx = 16;

		m_nICONcy = 16;
	}

	m_hICONArrow = m_MessageTypeImages.ExtractIcon(ICON_ARROW);

	SetDrawFocusRect();
}


CMenuButton::~CMenuButton()
{
}


BEGIN_MESSAGE_MAP(CMenuButton, CButton)

	//{{AFX_MSG_MAP(CMenuButton)
	ON_WM_MOUSEMOVE()
	ON_CONTROL_REFLECT_EX(BN_CLICKED, OnClicked)
	ON_MESSAGE(WM_MOUSELEAVE, OnMouseLeave)
	//}}AFX_MSG_MAP

END_MESSAGE_MAP()


/////////////////////////////////////////////////////////////////////////////

// CMenuButton message handlers

void CMenuButton::PreSubclassWindow() 
{
	CButton::PreSubclassWindow();

	ModifyStyle(0, BS_OWNERDRAW);
}


void CMenuButton::SetDrawFocusRect(BOOL bDrawFocusRect)
{
	m_bDrawFocusRect = bDrawFocusRect;
}


void CMenuButton::SetStyle(BOOL bDrawArrow)
{
	if (bDrawArrow)
	{
		ModifyStyle(0, BS_OWNERDRAW, SWP_NOMOVE|SWP_NOZORDER|SWP_NOSIZE);
	}
	else
	{
		ModifyStyle(BS_OWNERDRAW, 0, SWP_NOMOVE|SWP_NOZORDER|SWP_NOSIZE);
	}
}


BOOL CMenuButton::OnClicked() 
{
	const INT_PTR nSize = m_aryMessages.GetSize();

	if (nSize > 1)
	{
		CCAMenu t_Menu;
		t_Menu.CreatePopupMenu();

		CItemInfo *pItem = new CItemInfo[nSize];

		int nMaxWidth = 0;

		for (int i=0; i<nSize; i++)
		{
			memset(pItem[i].sText, 0, sizeof(pItem[i].sText));
			for (int j=0; j<m_aryMessages[i].strMessage.GetLength() && j < MESSAGE_MAX_LEN - 1; j++)
			{
				pItem[i].sText[j] = m_aryMessages[i].strMessage[j];
			}

			HICON hIcon = m_MessageTypeImages.ExtractIcon(m_aryMessages[i].uImageIndex);
			pItem[i].hIconNormal = hIcon;
			pItem[i].hIconSelect = hIcon;

			t_Menu.AppendMenu(MF_OWNERDRAW, i, (LPCTSTR)(pItem+i));

			CDC *pDC = this->GetDC();

			CSize size = pDC->GetTextExtent(m_aryMessages[i].strMessage);

			if (nMaxWidth < size.cx)
			{
				nMaxWidth = size.cx;
			}
		}

		t_Menu.SetWidth(nMaxWidth);

		CRect rect;
		GetWindowRect(rect);

		t_Menu.TrackPopupMenu(TPM_RIGHTBUTTON, rect.left, rect.bottom, this);

		delete []pItem;
	}

	return FALSE;
}


void CMenuButton::AddMessage(MESSAGE_ITEM item)
{
	INT_PTR nSize = m_aryMessages.GetSize();

	m_aryMessages.Add(item);

	if (nSize == 0)
	{
		Invalidate();
	}

	if (nSize == 1)//Draw the Arrow
	{
		Invalidate();
	}
}

BOOL CMenuButton::RemoveMessage(int nIndex)
{
	INT_PTR nSize = m_aryMessages.GetSize();
	if (nIndex >= 0 && nIndex < nSize)
	{
		m_aryMessages.RemoveAt(nIndex);

		if (nIndex == 0)
		{
			RedrawWindow();
		}

		return TRUE;
	}

	return FALSE;
}

void CMenuButton::RemoveAllMessage()
{
	m_aryMessages.RemoveAll();

	Invalidate();
}


void CMenuButton::DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct)
{
	CDC *pDC = CDC::FromHandle(lpDrawItemStruct->hDC);
	CRect rectItem = lpDrawItemStruct->rcItem;

	INT_PTR nSize = m_aryMessages.GetSize();

	if (nSize > 0)
	{
		HICON hIcon = m_MessageTypeImages.ExtractIcon(m_aryMessages[0].uImageIndex);
		CString sText = m_aryMessages.GetAt(0).strMessage;

		CRect rcIcon = lpDrawItemStruct->rcItem;
		rcIcon.left = lpDrawItemStruct->rcItem.left + 1;
		rcIcon.right = rcIcon.left + m_nICONcx;

//		TRACE(_T("\nrcItem.left=%d, rcItem.right=%d"), lpDrawItemStruct->rcItem.left, lpDrawItemStruct->rcItem.right);

		int y1 = (lpDrawItemStruct->rcItem.bottom - lpDrawItemStruct->rcItem.top - m_nICONcy) / 2;
		rcIcon.top += y1;
		rcIcon.bottom = rcIcon.top + m_nICONcy;

//		TRACE(_T("\nrcIcon.left=%d, rcIcon.right=%d"), rcIcon.left, rcIcon.right);

		//Draw Text//////////////////////////////////////////////////////////////////////////
		CSize Textsize = pDC->GetTextExtent(sText);
		CRect rectText = lpDrawItemStruct->rcItem;
		rectText.left = rcIcon.right + 3;
		rectText.right = rcIcon.right + Textsize.cx + 3;

//		TRACE(_T("\nrectText.left=%d, rectText.right=%d"), rectText.left, rectText.right);

		//Adjust the button size
		CRect rect = lpDrawItemStruct->rcItem;
		rect.right = rectText.right + 1;

		CRect rcArrowIcon = rectText;

		COLORREF cBackground = m_cBackground;
		
		if (nSize > 1)
		{
			//Draw Arraow ICON//////////////////////////////////////////////////////////////////////////
			rcArrowIcon.left =  rectText.right + 10;
			rcArrowIcon.right =  rcArrowIcon.left + m_nICONcx;

			int y2 = (lpDrawItemStruct->rcItem.bottom - lpDrawItemStruct->rcItem.top - m_nICONcy) / 2;
			rcArrowIcon.top += y2;

//			TRACE(_T("\nrcArrowIcon.left=%d, rcArrowIcon.right=%d"), rcArrowIcon.left, rcArrowIcon.right);

			rect.right = rcArrowIcon.right + 1;

			cBackground = m_bMouseOverButton ? DEFAULT_HOT_COLOR : m_cBackground;
		}
		else
		{
			cBackground = m_bMouseOverButton ? DEFAULT_HOT_COLOR : m_cBackground;
		}

		SetWindowPos(&wndTopMost, rect.left, rect.top, rect.Width(), rect.Height(), SWP_NOZORDER|SWP_NOMOVE);

		rect.bottom -= 1;

		pDC->FillSolidRect(rect.left, rect.top, rect.Width(), rect.Height(), cBackground);

		pDC->SetBkMode(TRANSPARENT);

		::DrawIconEx(pDC->m_hDC, rcIcon.left, rcIcon.top, hIcon, m_nICONcx, m_nICONcy, 0, NULL, DI_NORMAL);

		pDC->DrawText(sText, &rectText, DT_LEFT | DT_VCENTER | DT_SINGLELINE);

		if (nSize > 1)
		{
			::DrawIconEx(pDC->m_hDC, rcArrowIcon.left, rcArrowIcon.top, m_hICONArrow, m_nICONcx, m_nICONcy, 0, NULL, DI_NORMAL);
		}
	}
}


void CMenuButton::OnMouseMove(UINT nFlags, CPoint point)
{
	CPoint pt(point);
	ClientToScreen(&pt);

	CRect rect;
	GetWindowRect(&rect);

	BOOL bMouseOverButton = m_bMouseOverButton;

	if (rect.PtInRect(pt))
		m_bMouseOverButton = TRUE;
	else
		m_bMouseOverButton = FALSE;

	if (bMouseOverButton != m_bMouseOverButton)
	{
		InvalidateRect(NULL);

		if (m_bMouseOverButton)
		{
			// mouse is now over button
			TRACKMOUSEEVENT tme;
			tme.cbSize    = sizeof(tme);
			tme.dwFlags   = TME_LEAVE;
			tme.hwndTrack = m_hWnd;
			::_TrackMouseEvent(&tme);
		}
	}

	CButton::OnMouseMove(nFlags, point);
}


LRESULT CMenuButton::OnMouseLeave(WPARAM /*wParam*/, LPARAM /*lParam*/)
{
	m_bMouseOverButton = FALSE;

	RedrawWindow();

	return 0;
}
