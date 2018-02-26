#pragma once

// ProductListCtrl.h : header file
//
#include "CheckHeadCtrl.h"
/////////////////////////////////////////////////////////////////////////////
// CProductListCtrl window
#define WM_ITEMCHECKBOXCHANGED				WM_USER+202

class CProductListCtrl : public CListCtrl
{
// Construction
public:
	CProductListCtrl();

// Attributes
public:

// Operations
private:

	CCheckHeadCtrl m_objHeadCtrl;
	CToolTipCtrl m_tooltip;

	LOGFONT m_lf;
	CFont m_font;

	BOOL m_blInited;
	CImageList m_checkImgList;

public:
	BOOL Init();

	BOOL AddHeaderToolTip(int nCol, LPCTSTR sTip = NULL);
	
public:
// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CProductListCtrl)
	virtual void PreSubclassWindow();
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CProductListCtrl();

	// Generated message map functions
protected:
	//{{AFX_MSG(CProductListCtrl)
	afx_msg void OnItemChanged(NMHDR* pNMHDR, LRESULT* pResult);
	BOOL OnNotify(WPARAM wParam, LPARAM lParam,LRESULT* pResult);
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()
};