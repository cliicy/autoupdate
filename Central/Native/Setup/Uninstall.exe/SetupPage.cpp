// SetupPage.cpp : implementation file
//

#include "stdafx.h"
#include "Uninstall.h"
#include "SetupPage.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

/////////////////////////////////////////////////////////////////////////////
// CSetupPage property page

IMPLEMENT_DYNCREATE(CSetupPage, CSetupPage_Base)

CSetupPage::CSetupPage() : CSetupPage_Base(m_nIDTemplate)
{
	//{{AFX_DATA_INIT(CSetupPage)
	// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}

CSetupPage::CSetupPage(UINT nIDTemplate, 
					   UINT nIDCaption/*= 0*/, 
					   UINT nIDHeaderTitle/*= 0*/, 
					   UINT nIDHeaderSubTitle/*= 0*/)
					   : CSetupPage_Base(nIDTemplate, nIDCaption, nIDHeaderTitle, nIDHeaderSubTitle) ,
m_nIDTemplate(nIDTemplate)
{
	this->m_psp.dwFlags &= ~(PSP_HASHELP);
}


CSetupPage::~CSetupPage()
{
}

void CSetupPage::DoDataExchange(CDataExchange* pDX)
{
	CSetupPage_Base::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSetupPage)
	// NOTE: the ClassWizard will add DDX and DDV calls here
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CSetupPage, CSetupPage_Base)
	//{{AFX_MSG_MAP(CSetupPage)
	ON_WM_CTLCOLOR()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSetupPage message handlers

BOOL CSetupPage::OnInitDialog() 
{
	CSetupPage_Base::OnInitDialog();

	CBitmap bitmap;
	bitmap.LoadBitmap(IDB_WATERMARK256);

	m_brush.CreatePatternBrush(&bitmap);

	m_nullBrush.CreateStockObject(NULL_BRUSH);

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

// we use this to print the property page, we are not passing in the bmp 
// into the property sheet itself
HBRUSH CSetupPage::OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor) 
{
	HBRUSH hbr = CSetupPage_Base::OnCtlColor(pDC, pWnd, nCtlColor);

	m_OldCPalette = pDC->SelectPalette(&m_CPalette, FALSE);
	pDC->RealizePalette();

	// CTLCOLOR_LISTBOX for the combo box list, so it will be white
	// CTLCOLOR_EDIT for the tree ctrl, so it will be white
	if ((nCtlColor == CTLCOLOR_LISTBOX)||(nCtlColor == CTLCOLOR_EDIT))   
	{
		return hbr;
	}

	pDC->SetBkMode(TRANSPARENT);

	return (HBRUSH)(m_brush.m_hObject);
}


BOOL CSetupPage::OnQueryCancel() 
{
	CString strTitle, strMsg;
	strTitle.LoadString(IDS_STRING_APPLICATION_TITLE);

	strMsg.LoadString(IDS_SETUP_CANCEL);

	BOOL bRet = (IDYES == MessageBox(strMsg, strTitle, MB_YESNO|MB_ICONQUESTION|MB_DEFBUTTON2));

	return bRet;
}