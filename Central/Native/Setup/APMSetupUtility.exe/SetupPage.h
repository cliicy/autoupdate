#pragma once
#include "NavigatePropertyPage.h"
// SetupPage.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CSetupPage dialog

#define CSetupPage_Base CNavigatePropertyPage

class CSetupPage : public CSetupPage_Base
{
	DECLARE_DYNCREATE(CSetupPage)

// Construction
public:
	CSetupPage();
	CSetupPage (UINT nIDTemplate, UINT nIDCaption = 0, UINT nIDHeaderTitle = 0, UINT nIDHeaderSubTitle = 0);
	~CSetupPage();

	UINT m_nIDTemplate;

protected:
// Overrides
	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(CSetupPage)
	//virtual BOOL OnQueryCancel();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	virtual BOOL OnInitDialog();
	//}}AFX_VIRTUAL

protected:
	// Generated message map functions
	//{{AFX_MSG(CSetupPage)
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()

protected:
	CPalette m_CPalette;				// use palette for 256 color devices
	CPalette* m_OldCPalette;

	CBrush m_brush;			// use to paint the property page
	CBrush m_nullBrush;		// use to paint the uid and pid text field, to make 
};
