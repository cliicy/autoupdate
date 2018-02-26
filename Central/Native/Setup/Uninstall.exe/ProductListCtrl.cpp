// ProductListCtrl.cpp : implementation file

#include "stdafx.h"
#include "ProductListCtrl.h"
#include "resource.h"
#include "Uninstall.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

/////////////////////////////////////////////////////////////////////////////
// CProductListCtrl

CProductListCtrl::CProductListCtrl() : m_blInited(FALSE)
{
}

CProductListCtrl::~CProductListCtrl()
{
}


BEGIN_MESSAGE_MAP(CProductListCtrl, CListCtrl)
	//{{AFX_MSG_MAP(CProductListCtrl)
	ON_NOTIFY_REFLECT(LVN_ITEMCHANGED, OnItemChanged)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CProductListCtrl message handlers
void CProductListCtrl::OnItemChanged(NMHDR* pNMHDR, LRESULT* pResult)
{
	NMLISTVIEW* pNMLV = (NMLISTVIEW*)pNMHDR;
	*pResult = 0;

	NM_LISTVIEW* pNMListView = (NM_LISTVIEW*)pNMHDR;
	*pResult = 0;

	if (pNMListView->uOldState == 0 && pNMListView->uNewState == 0)
		return;//No change

	BOOL bPrevState = (BOOL)(((pNMListView->uOldState & LVIS_STATEIMAGEMASK) >> 12) - 1);//Old checkbox state
	if (bPrevState < 0)//On startup there's no previous state
		bPrevState = 0;//so assign as false (unchecked)

	//New checkbox state
	BOOL bChecked = (BOOL)(((pNMListView->uNewState & LVIS_STATEIMAGEMASK) >> 12) - 1);
	if (bChecked < 0)//On non-checkbox notifications assume false
		bChecked = 0;

	if (bPrevState == bChecked)//No change in checkbox 
		return;

	//Now bChecked holds the new checkbox state
	TRACE(_T("%d checkbox state is changed from %d to %d.\n"), pNMListView->iItem, bPrevState, bChecked);
	
	if (m_blInited && LVIF_STATE == pNMLV->uChanged)
	{
		BOOL blAllChecked = TRUE;
		int nCount = GetItemCount();

		for (int nItem = 0; nItem < nCount; nItem++)
		{
			if (!ListView_GetCheckState(GetSafeHwnd(), nItem))
			{
				blAllChecked = FALSE;
				break;
			}
		}

		HDITEM hdItem;
		hdItem.mask = HDI_IMAGE;
		m_objHeadCtrl.GetItem(0, &hdItem);

		if (((hdItem.iImage == CHECK) && !blAllChecked) || ((hdItem.iImage != CHECK) && blAllChecked))
		{
			if (blAllChecked)
				hdItem.iImage = CHECK;
			else
				hdItem.iImage = UNCHECK;

			VERIFY(m_objHeadCtrl.SetItem(0, &hdItem));
		}
	}

	CWnd *pParent = this->GetParent();
	if (pParent)
	{
		::SendMessage(pParent->GetSafeHwnd(), WM_ITEMCHECKBOXCHANGED, pNMListView->iItem, bChecked);
	}
}

void CProductListCtrl::PreSubclassWindow()
{
	CListCtrl::PreSubclassWindow();

	// Add initialization code
	m_tooltip.Create(this);
}

BOOL CProductListCtrl::PreTranslateMessage(MSG* pMsg)
{
	m_tooltip.RelayEvent(pMsg);

	return CListCtrl::PreTranslateMessage(pMsg);
}

BOOL CProductListCtrl::AddHeaderToolTip(int nCol, LPCTSTR sTip /*= NULL*/)
{
	const int TOOLTIP_LENGTH = 80;
	TCHAR buf[TOOLTIP_LENGTH+1] = {0};

	CHeaderCtrl* pHeader = (CHeaderCtrl*)GetDlgItem(0);
	int nColumnCount = pHeader->GetItemCount();

	if (nCol >= nColumnCount)
		return FALSE;

	if ((GetStyle() & LVS_TYPEMASK) != LVS_REPORT)
		return FALSE;

	// Get the header height
	RECT rect;
	pHeader->GetClientRect(&rect);
	int height = rect.bottom;

	RECT rctooltip;
	rctooltip.top = 0;
	rctooltip.bottom = rect.bottom;

	// Now get the left and right border of the column
	rctooltip.left = 0 - GetScrollPos(SB_HORZ);
	for (int i = 0; i < nCol; i++)
		rctooltip.left += GetColumnWidth(i);
	rctooltip.right = rctooltip.left + GetColumnWidth(nCol);

	if (NULL == sTip)
	{
		// Get column heading
		LV_COLUMN lvcolumn;
		lvcolumn.mask = LVCF_TEXT;
		lvcolumn.pszText = buf;
		lvcolumn.cchTextMax = TOOLTIP_LENGTH;
		if (!GetColumn(nCol, &lvcolumn))
			return FALSE;
	}

	m_tooltip.AddTool(GetDlgItem(0), sTip ? sTip : buf, &rctooltip, nCol+1);

	return TRUE;
}

BOOL CProductListCtrl::OnNotify(WPARAM wParam, LPARAM lParam, LRESULT* pResult)
{
	HD_NOTIFY *pHDN = (HD_NOTIFY*)lParam;

	if ((pHDN->hdr.code == HDN_ENDTRACKA || pHDN->hdr.code == HDN_ENDTRACKW))
	{
		// Update the tooltip info
		CHeaderCtrl* pHeader = (CHeaderCtrl*)GetDlgItem(0);
		int nColumnCount = pHeader->GetItemCount();

		CToolInfo toolinfo;
		toolinfo.cbSize = sizeof(toolinfo);

		// Cycle through the tooltipinfo for each effected column
		for (int i = pHDN->iItem; i <= nColumnCount; i++)
		{
			m_tooltip.GetToolInfo(toolinfo, pHeader, i + 1);

			int dx;// store change in width
			if (i == pHDN->iItem)
				dx = pHDN->pitem->cxy - (toolinfo.rect.right - toolinfo.rect.left);
			else
				toolinfo.rect.left += dx;
			toolinfo.rect.right += dx;
			m_tooltip.SetToolInfo(&toolinfo);
		}
	}

	return CListCtrl::OnNotify(wParam, lParam, pResult);
}

BOOL CProductListCtrl::Init()
{
	if (m_blInited)
		return TRUE;

	CHeaderCtrl* pHeadCtrl = this->GetHeaderCtrl();
	ASSERT(pHeadCtrl->GetSafeHwnd());

	VERIFY(m_objHeadCtrl.SubclassWindow(pHeadCtrl->GetSafeHwnd()));
	VERIFY(m_checkImgList.Create(IDB_HEAD_CHECKBOX, 16, 3, RGB(255, 0, 255)));
	::GetObject((HFONT)GetStockObject(DEFAULT_GUI_FONT), sizeof(m_lf), &m_lf);

	//set the header font
	m_lf.lfWeight = FW_BOLD;
	m_font.CreateFontIndirect(&m_lf);
	pHeadCtrl->SetFont(&m_font);

	//set the margin of check box on header
	pHeadCtrl->SetBitmapMargin(3);

	int i = m_checkImgList.GetImageCount();
	m_objHeadCtrl.SetImageList(&m_checkImgList);

	HDITEM hdItem;
	hdItem.mask = HDI_IMAGE | HDI_FORMAT;
	VERIFY(m_objHeadCtrl.GetItem(0, &hdItem));
	hdItem.iImage = 1;
	hdItem.fmt |= HDF_IMAGE;

	VERIFY(m_objHeadCtrl.SetItem(0, &hdItem));

	m_blInited = TRUE;

	return TRUE;
}