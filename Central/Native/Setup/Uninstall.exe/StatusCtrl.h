#pragma once

// StatusCtrl.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CStatusCtrl window

class CStatusCtrl : public CTreeCtrl
{
// Construction
public:
	CStatusCtrl();

// Attributes
public:

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CStatusCtrl)
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CStatusCtrl();

	// Generated message map functions
protected:
	//{{AFX_MSG(CStatusCtrl)
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnRButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnLButtonDblClk(UINT, CPoint);
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()
private:
};

/////////////////////////////////////////////////////////////////////////////
