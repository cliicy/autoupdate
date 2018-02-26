#pragma once

#include "DrawMessage.h"
#include <AfxTempl.h>

#define DEFAULT_STYLES			WS_BORDER|WS_CHILD|WS_VISIBLE|WS_VSCROLL|WS_TABSTOP

#define INVALID_ITEM			-1

enum {MESSAGELIST_STYLE_CHECKBOX=1, MESSAGELIST_STYLE_GRIDLINES=2, MESSAGELIST_STYLE_IMAGES=4};

enum {HTML_TEXT = 1, NORMAL_TEXT=2, SINGLE_LINE_TEXT=3};

#define AUTO						0

#define ITEM_PADDING_TOP			5
#define ITEM_PADDING_BOTTOM			5
#define ITEM_PADDING_LEFT			5

#define ITEM_PADDING_CHECKBOX_LEFT	3

#define ITEM_IMAGE_PADDING_LEFT		5
#define ITEM_IMAGE_PADDING_RIGHT	5

#define ITEM_CHECKBOX_WIDTH			16

#define NONE_SELECTED				-1

enum{MESSAGELIST_SELECTIONCHANGED=1,MESSAGELIST_LBUTTONDOWN, MESSAGELIST_RBUTTONDOWN, MESSAGELIST_LBUTTONDBLCLICK, MESSAGELIST_ITEMCHECKED};

/////////////////////////////////////////////////////////////////////////////

struct NM_MESSAGELISTCTRL
{
	NMHDR hdr;
	int nItemNo;
	LPARAM lItemData;
	BOOL bChecked;
	CString sItemText;
};

struct MESSAGELIST_ITEM
{
	MESSAGELIST_ITEM()
	{
		nItemNo = INVALID_ITEM;
		lItemData = 0;
		nHeight = 0;
		nStyle = NORMAL_TEXT;
		rcItem.SetRectEmpty();
		bChecked = FALSE;
		bHeightSpecified = FALSE;
	}

	int nItemNo;
	int nHeight;
	int nStyle;
	LPARAM lItemData;
	CRect rcItem;
	BOOL bChecked;
	BOOL bHeightSpecified;
	UINT uiImage;
	CString sItemText;
};

class CMessageListCtrl : public CWnd
{
// Construction
public:
	CMessageListCtrl();

// Attributes
private:
	MESSAGELIST_ITEM * GetInternalData(int nPos);

	CRect GetItemRect(int nPos);

	int CalculateItemHeight(CString sText,int nStyle,UINT uiImage,int nWidth);

	CList<MESSAGELIST_ITEM*, MESSAGELIST_ITEM*> m_listItems;

	CMap<int,int,MESSAGELIST_ITEM*, MESSAGELIST_ITEM*> m_mapItems;

	CFont m_font;

	CPen m_penLight;

	COLORREF m_clrBkSelectedItem;

	int m_nTotalItems;

	int m_nListHeight, m_nWndWidth, m_nWndHeight, m_nSelectedItem;
	
	BOOL m_bHasFocus;

	UINT m_nControlID;

	DWORD m_dwExtendedStyles;

	CImageList m_ImageList;

	CImageList *m_pImageList;
// Operations
public:
	INT_PTR	GetItemCount()
	{
		return m_listItems.GetCount();
	}

	void SetImageList(CImageList *pImageList)
	{
		m_pImageList = pImageList;
	}

	CImageList* GetImageList()
	{
		return m_pImageList;
	}
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMessageListCtrl)
	//}}AFX_VIRTUAL

// Implementation
public:
	void SetInternalData(int nPos,MESSAGELIST_ITEM *pData);

	BOOL IsRectVisible(CRect rcClipBox,CRect rcItem);

	void SendCheckStateChangedNotification(int nPos);

	void SendSelectionChangeNotification(int nPos);

	virtual void DrawItem(CDC *pDC,CRect rcItem, MESSAGELIST_ITEM *pItem, BOOL bSelected);

	void SetImage(int nPos,UINT uiImage);

	UINT GetImage(int nPos);

	void ReArrangeWholeLayout();

	void ReArrangeItems();

	BOOL DeleteItem(int nPos);

	void SetItemText(int nPos,CString sItemText,BOOL bCalculateHeight = FALSE);

	BOOL GetItemCheck(int nPos);

	void SetItemCheck(int nPos,BOOL bChecked = TRUE);

	void EnsureVisible(int nPos);

	DWORD GetExtendedStyle();

	void SetExtendedStyle(DWORD dwExStyle);

	CString GetItemText(int nPos);

	int GetSelectedItem();

	void SetItemData(int nPos,LPARAM lItemData);

	LPARAM GetItemData(int nPos);

	void DeleteAllItems();

	int InsertItem(CString sText,UINT uiImage,int nStyle=HTML_TEXT,int nHeight=AUTO);

	BOOL Create(CWnd *pParent, CRect rc, UINT nID, DWORD dwStyle=DEFAULT_STYLES);

	virtual ~CMessageListCtrl();

	// Generated message map functions
protected:
	//{{AFX_MSG(CMessageListCtrl)
	afx_msg BOOL OnMouseWheel( UINT nFlags, short zDelta, CPoint pt );
	afx_msg void OnPaint();
	afx_msg BOOL OnEraseBkgnd(CDC* pDC);
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnVScroll(UINT nSBCode, UINT nPos, CScrollBar* pScrollBar);
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg void OnSetFocus(CWnd* pOldWnd);
	afx_msg void OnKillFocus(CWnd* pNewWnd);
	afx_msg UINT OnGetDlgCode();
	afx_msg void OnKeyDown(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnRButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnLButtonDblClk(UINT nFlags, CPoint point);
	afx_msg void OnDestroy();
	afx_msg BOOL OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message);
	//}}AFX_MSG
	
	DECLARE_MESSAGE_MAP()
};