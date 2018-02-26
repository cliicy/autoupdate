#pragma once
// CheckHeadCtrl.h : header file

/////////////////////////////////////////////////////////////////////////////
// CCheckHeadCtrl window

enum CHECK_STATUS {UNKNOWN, UNCHECK, CHECK};

class CCheckHeadCtrl : public CHeaderCtrl
{
// Construction
public:
	CCheckHeadCtrl();

// Attributes
public:

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CCheckHeadCtrl)
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CCheckHeadCtrl();

protected:

	// Generated message map functions
protected:
	//{{AFX_MSG(CCheckHeadCtrl)
	afx_msg void OnItemClicked(NMHDR* pNMHDR, LRESULT* pResult);
		// NOTE - the ClassWizard will add and remove member functions here.
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()
};