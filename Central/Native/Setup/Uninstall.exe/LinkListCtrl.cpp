// CLinkListCtrl

#include "stdafx.h"
#include "LinkListCtrl.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

IMPLEMENT_DYNAMIC(CLinkListCtrl, CListCtrl)

CLinkListCtrl::CLinkListCtrl()
{

}


CLinkListCtrl::~CLinkListCtrl()
{
	map<int, stURLTAG*>::iterator pos;

	for (pos = m_mapURL.begin(); pos != m_mapURL.end(); pos++)
	{
		delete pos->second;
	}
}


BEGIN_MESSAGE_MAP(CLinkListCtrl, CListCtrl)
	ON_WM_MOUSEMOVE()
	ON_NOTIFY_REFLECT(NM_CUSTOMDRAW, &CLinkListCtrl::OnNMCustomdraw)
	ON_WM_LBUTTONDOWN()
END_MESSAGE_MAP()


CRect CLinkListCtrl::GetTextRect(int nItem, int nSubItem)
{
	ASSERT(nItem != -1);
	
	CDC *pDC = GetDC();
	CFont *pOldFont = pDC->SelectObject(GetFont());
	CSize size = pDC->GetTextExtent(GetItemText(nItem, nSubItem));
	CRect rect;

	pDC->SelectObject(pOldFont);
	
	ReleaseDC(pDC);

	GetSubItemRect(nItem, nSubItem, LVIR_LABEL, rect);
	
	rect.DeflateRect(6, (rect.Height() - size.cy) / 2);
	
	HDITEM hditem;
	hditem.mask = HDI_FORMAT;
	
	GetHeaderCtrl()->GetItem(nSubItem, &hditem);
	
	int nFMT = hditem.fmt & HDF_JUSTIFYMASK;

	switch (nFMT)
	{
	case HDF_CENTER:
		rect.DeflateRect((rect.Width()-size.cx) / 2, 0);
		break;
	case HDF_RIGHT:
		rect.left = rect.right - size.cx;
		break;
	default: // other formats are regarded as LVCFMT_LEFT
		rect.right = rect.left + size.cx;
	}

	return rect;
}


BOOL CLinkListCtrl::PtInText(CPoint pt, int nItem, int nSubItem)
{
	return GetTextRect(nItem, nSubItem).PtInRect(pt);
}

BOOL CLinkListCtrl::IsURL(int nItem, int nSubItem)
{
	return (m_mapURL.find(nItem * 100 + nSubItem) != m_mapURL.end());
}


BOOL CLinkListCtrl::SetItemURL(int nItem, int nSubItem, CString strURL, CString strTooltip)
{
	if (nItem < GetItemCount() && nItem >= 0 
		&& nSubItem < GetHeaderCtrl()->GetItemCount() && nSubItem >= 0)
	{
		stURLTAG *p = new stURLTAG;
		p->IsHoverOn = FALSE;
		p->strURL = strURL;
		p->strTooltip = strTooltip;

		m_mapURL.insert(make_pair(nItem * 100 + nSubItem, p));

		return TRUE;
	}
	else
		return FALSE;
}


void CLinkListCtrl::ClearItemURL(int nItem, int nSubItem)
{
	stURLTAG *p = m_mapURL[nItem * 100 + nSubItem];
	
	delete p;

	m_mapURL.erase(nItem*100+nSubItem);
}


void CLinkListCtrl::RedrawSubItem(int nItem, int nSubItem, BOOL IsHoverOn)
{
	CRect rect;
	
	stURLTAG *p = m_mapURL[nItem * 100 + nSubItem];
	p->IsHoverOn = IsHoverOn;
	
	GetSubItemRect(nItem, nSubItem, LVIR_LABEL, rect);

	m_toolTip.AddTool(this, p->strTooltip, GetTextRect(nItem, nSubItem), 1);
	
	RedrawWindow(&rect);
}


void CLinkListCtrl::OnMouseMove(UINT nFlags, CPoint point)
{
	LVHITTESTINFO NowHti;
	static LVHITTESTINFO PreHti;
	NowHti.pt = point;
	const int nItem = SubItemHitTest(&NowHti);
	BOOL NowIn = FALSE;
	static BOOL PreIn;

	if (nItem != -1 && IsURL(NowHti.iItem, NowHti.iSubItem) && PtInText(point, NowHti.iItem, NowHti.iSubItem))
		NowIn = TRUE;
	else
		NowIn = FALSE;

	if (PreHti.iItem != NowHti.iItem || PreHti.iSubItem != NowHti.iSubItem)
	{
		if (IsURL(PreHti.iItem, PreHti.iSubItem) && PreIn)
			RedrawSubItem(PreHti.iItem, PreHti.iSubItem, FALSE);

		if (IsURL(NowHti.iItem, NowHti.iSubItem) && NowIn)
			RedrawSubItem(NowHti.iItem, NowHti.iSubItem, TRUE);
	}

	if (PreIn != NowIn)
	{
		PreIn ? RedrawSubItem(PreHti.iItem, PreHti.iSubItem, FALSE)
			  : RedrawSubItem(NowHti.iItem, NowHti.iSubItem, TRUE);
	}

	PreHti = NowHti;
	PreIn = NowIn;

	CListCtrl::OnMouseMove(nFlags, point);
}


void CLinkListCtrl::OnNMCustomdraw(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMLVCUSTOMDRAW lplvcd = (LPNMLVCUSTOMDRAW)pNMHDR;

	if (lplvcd->nmcd.dwDrawStage == CDDS_PREPAINT)
	{
		*pResult = CDRF_NOTIFYITEMDRAW;
	}
	else if (lplvcd->nmcd.dwDrawStage == CDDS_ITEMPREPAINT)
	{
		*pResult = CDRF_NOTIFYSUBITEMDRAW;
	}
	else if (lplvcd->nmcd.dwDrawStage == (CDDS_ITEMPREPAINT | CDDS_SUBITEM))
	{
		if (m_mapURL.find((int)(lplvcd->nmcd.dwItemSpec * 100 + lplvcd->iSubItem)) != m_mapURL.end())
		{
			stURLTAG *p = m_mapURL[(int)(lplvcd->nmcd.dwItemSpec * 100 + lplvcd->iSubItem)];

			if (p->IsHoverOn)
			{
				::SelectObject(lplvcd->nmcd.hdc, m_ftUnderline.m_hObject);
				lplvcd->clrText = RGB(255, 0, 0);
			} 
			else
			{
				::SelectObject(lplvcd->nmcd.hdc, m_ftURL.m_hObject);
				lplvcd->clrText = RGB(0, 0, 255);
			}
		}

		*pResult = CDRF_DODEFAULT;
	}
}


void CLinkListCtrl::PreSubclassWindow()
{
	CFont* pFont = GetFont();

	if (!pFont)
	{
		HFONT hFont = (HFONT)GetStockObject(DEFAULT_GUI_FONT);

		if (hFont == NULL)
			hFont = (HFONT) GetStockObject(ANSI_VAR_FONT);

		if (hFont)
			pFont = CFont::FromHandle(hFont);
	}

	ASSERT(pFont->GetSafeHandle());
	
	LOGFONT lf;
	pFont->GetLogFont(&lf);
	lf.lfUnderline = (BYTE) TRUE;
	m_ftURL.CreateFontIndirect((LOGFONT*)&lf);
	m_ftUnderline.CreateFontIndirect((LOGFONT*)&lf);
	m_toolTip.Create(this);

	CListCtrl::PreSubclassWindow();
}


void CLinkListCtrl::OnLButtonDown(UINT nFlags, CPoint point)
{
	LVHITTESTINFO hti;
	hti.pt = point;
	const int IDX = SubItemHitTest(&hti);
	
	if (IsURL(IDX, hti.iSubItem) && PtInText(point, IDX, hti.iSubItem))
	{
		stURLTAG *p = m_mapURL[IDX * 100 + hti.iSubItem];

		ShellExecute(NULL, _T("open"), p->strURL, NULL, NULL, SW_SHOW);
	}

	CListCtrl::OnLButtonDown(nFlags, point);
}


BOOL CLinkListCtrl::PreTranslateMessage(MSG* pMsg)
{
	m_toolTip.RelayEvent(pMsg);

	return CListCtrl::PreTranslateMessage(pMsg);
}
