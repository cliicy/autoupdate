#pragma once
// WizSheet.h : header file

#include "NavigatePropertySheet.h"
#include "ProductListPage.h"
#include "MessagePage.h"
#include "SetupComponentPage.h"
#include <atlimage.h>

/////////////////////////////////////////////////////////////////////////////
// CSetupSheet

#define CSetupSheet_Base CNavigatePropertySheet

class CSetupSheet : public CSetupSheet_Base
{
	DECLARE_DYNAMIC(CSetupSheet)

	// Construction
public:
	
	CSetupSheet(UINT nIDCaption, CWnd* pParentWnd = NULL,
			UINT iSelectPage = 0, HBITMAP hbmWatermark = NULL,
			HPALETTE hpalWatermark = NULL, HBITMAP hbmHeader = NULL);

	
	CSetupSheet(LPCTSTR pszCaption, CWnd* pParentWnd = NULL,
			UINT iSelectPage = 0, HBITMAP hbmWatermark = NULL,
			HPALETTE hpalWatermark = NULL, HBITMAP hbmHeader = NULL);

	virtual ~CSetupSheet();

// Attributes
private:
	void Init();

protected:
	HICON m_hIcon;
	CImage m_objHeaderImage;

	CPalette m_CPalette;				// use palette for 256 color devices
	CPalette* m_OldCPalette;

	CBrush m_brush;			// use to paint the property page
	CBrush m_nullBrush;		// use to paint the uid and pid text field, to make 

	CProductListPage m_objProductList;

	CMessagePage m_objMessage;

	CSetupComponentPage m_objSetupComponents;

// Operations
public:
// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CSetupSheet)
	virtual BOOL OnInitDialog();
	//}}AFX_VIRTUAL

// Implementation
	// Generated message map functions
protected:
	//{{AFX_MSG(CSetupSheet)
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);
	afx_msg BOOL OnEraseBkgnd(CDC* pDC);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};
