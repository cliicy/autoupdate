// CAMenu.cpp: implementation of the CCAMenu class.


//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "CAMenu.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

#ifndef GRAY_AREA_LENGTH
#define GRAY_AREA_LENGTH 0x00000019
#endif

#define MIN_WIDTH		100
#define DEFAULT_WIDTH	400
#define DEFAULT_HEIGEIT 22

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CCAMenu::CCAMenu()
{
	m_nWidth = DEFAULT_WIDTH;
	m_nHeight = DEFAULT_HEIGEIT;
}

CCAMenu::~CCAMenu()
{}

// draw the menu
void CCAMenu::DrawItem (LPDRAWITEMSTRUCT lpDIS)
{
	CDC* pDC = CDC::FromHandle(lpDIS->hDC);
	VERIFY(pDC);
	pDC->SetBkMode(TRANSPARENT);

	CRect rcItem = lpDIS->rcItem;

	UINT uState = lpDIS->itemState;

	if (lpDIS->itemData == NULL)
		return;

	HICON hIconNormal = ((CItemInfo*)(lpDIS->itemData))->hIconNormal;
	HICON hIconSelect = ((CItemInfo*)(lpDIS->itemData))->hIconSelect;
	CString strText = ((CItemInfo*)(lpDIS->itemData))->sText;

	CRect rcIcon(rcItem);
	rcIcon.right = rcIcon.left + GRAY_AREA_LENGTH;
	CRect rcText(rcItem);
	rcText.left  = rcIcon.right;
	rcText.right  = rcText.left + m_nWidth;

	// draw background and icon
	if (uState & ODS_SELECTED)
	{
		DrawBgClr(pDC, rcItem, TRUE);
		DrawIcon(pDC, rcIcon, hIconNormal, hIconSelect, TRUE);
	}
	else
	{
		DrawBgClr(pDC, rcItem, FALSE);
		DrawIcon(pDC, rcIcon, hIconNormal, hIconSelect, FALSE);
	}

	// draw text
	DrawText(pDC, rcText, strText);
}

// draw background color
void CCAMenu::DrawBgClr(CDC* pDC, CRect rect, BOOL bSelected)
{
	if (bSelected)
	{
		pDC->SelectStockObject(NULL_BRUSH);
		pDC->SelectStockObject(BLACK_PEN);
		pDC->Rectangle(&rect);
		
		rect.DeflateRect(0x00000001, 0x00000001);
		pDC->FillSolidRect(&rect, RGB(0x00000000, 0x00000000, 0x00000000));
	}
	else
	{
		CRect rcGray(rect);
		rcGray.right = rcGray.left + GRAY_AREA_LENGTH;
		pDC->FillSolidRect(&rcGray, RGB(0x00000000, 0x00000000, 0x00000000));

		CRect rcWhite(rect);
		rcWhite.left = rcGray.right;
		pDC->FillSolidRect(&rcWhite, RGB(0x000000F9, 0x000000F8, 0x000000F7));
	}
}

// draw icon for menu items
void CCAMenu::DrawIcon(CDC* pDC, CRect rect, HICON hIconNormal, HICON hIconSelect, BOOL bSelected)
{
	rect.DeflateRect(0x03, 0x03, 0x03, 0x03);

	if (bSelected)
	{
		HICON hIcon = hIconSelect;
		ASSERT(hIcon);

		rect.OffsetRect(-1, -1);

		::DrawIconEx(pDC->m_hDC, rect.left, rect.top, 
			hIcon, 16, 16, 0, NULL, DI_NORMAL);
	}
	else
	{
		HICON hIcon = hIconNormal;
		ASSERT(hIcon);

		::DrawIconEx(pDC->m_hDC, rect.left, rect.top, 
			hIcon, 16, 16, 0, NULL, DI_NORMAL);
	}
}

// draw text for menu items
void CCAMenu::DrawText(CDC* pDC, CRect rect, CString sText)
{
	rect.DeflateRect(0x08, 0x00, 0x00, 0x00);

	pDC->DrawText(sText, &rect, DT_LEFT | DT_VCENTER | DT_SINGLELINE);
}

void CCAMenu::MeasureItem(LPMEASUREITEMSTRUCT lpMIS)
{
	lpMIS->itemWidth = m_nWidth;

	lpMIS->itemHeight = m_nHeight;
}


void CCAMenu::SetWidth(int nWidth)
{
	m_nWidth = nWidth > MIN_WIDTH ? nWidth : MIN_WIDTH;
}