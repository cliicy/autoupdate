// SetupSheet.cpp : implementation file

#include "stdafx.h"
#include "APMSetupUtility.h"
#include "SetupSheet.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


#if (_WIN32_IE >= 0x0400)
#define MY_WIZARD_STYLE            0x00002000
#endif


/////////////////////////////////////////////////////////////////////////////
// CSetupSheet

IMPLEMENT_DYNAMIC(CSetupSheet, CSetupSheet_Base)

CSetupSheet::CSetupSheet(UINT nIDCaption, CWnd* pParentWnd,
						 UINT iSelectPage, HBITMAP hbmWatermark, HPALETTE hpalWatermark,
						 HBITMAP hbmHeader)
						 : CSetupSheet_Base(nIDCaption, pParentWnd, iSelectPage,
						 hbmWatermark, hpalWatermark, hbmHeader)
{
	Init();
	if (hbmHeader)
		m_objHeaderImage.Attach(hbmHeader);
}


CSetupSheet::CSetupSheet(LPCTSTR pszCaption, CWnd* pParentWnd,
						 UINT iSelectPage, HBITMAP hbmWatermark, HPALETTE hpalWatermark,
						 HBITMAP hbmHeader)
						 : CSetupSheet_Base(pszCaption, pParentWnd, iSelectPage,
						 hbmWatermark, hpalWatermark, hbmHeader)

{
	Init();
	if (hbmHeader)
		m_objHeaderImage.Attach(hbmHeader);
}


CSetupSheet::~CSetupSheet()
{
	if (!m_objHeaderImage.IsNull())
		m_objHeaderImage.Detach();
}


BEGIN_MESSAGE_MAP(CSetupSheet, CSetupSheet_Base)
	//{{AFX_MSG_MAP(CSetupSheet)
	ON_WM_CTLCOLOR()
	ON_WM_ERASEBKGND()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSetupSheet message handlers
void CSetupSheet::Init()
{
	AddPage(&m_objSetupComponents);

	// set the WIZARD97 flag so we'll get the new look
	m_psh.dwFlags |= MY_WIZARD_STYLE;//SetWizardMode();

	m_psh.dwFlags &= ~(PSH_WIZARDCONTEXTHELP);	// take out help button
	m_psh.dwFlags &= ~(PSH_HASHELP);

	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}
BOOL CSetupSheet::OnEraseBkgnd(CDC* pDC) 
{
	BOOL bResult = CSetupSheet_Base::OnEraseBkgnd(pDC);

	if (!m_objHeaderImage.IsNull())
	{
		CPropertyPageEx* pPage = (CPropertyPageEx*)GetActivePage( );
		if (NULL != pPage)
		{
			CWnd* pTopBorder = GetDlgItem(0x3027);
			if (NULL != pTopBorder)
			{
				CRect rectTopBorder;
				pTopBorder->GetWindowRect(rectTopBorder);

				CRect rc;
				GetClientRect(rc);

				ScreenToClient(rectTopBorder);

				rc.bottom = rectTopBorder.top - 1;

				int nWidth, nHeight;
				if (m_objHeaderImage.GetWidth() > rc.Width())
					nWidth = rc.Width();
				else
					nWidth = m_objHeaderImage.GetWidth();

				if (m_objHeaderImage.GetHeight() > rc.Height())
					nHeight = rc.Height();
				else
					nHeight = m_objHeaderImage.GetHeight();

				m_objHeaderImage.Draw(pDC->m_hDC, 0, 0, rc.Width(), rc.Height(), 0, 0, nWidth, nHeight);
			}
		}
	}
	return bResult;
}

BOOL CSetupSheet::OnInitDialog() 
{
	BOOL blRet = CSetupSheet_Base::OnInitDialog();

	//CMenu* pMenu = this->GetSystemMenu(FALSE);
	//if (pMenu)
	//	pMenu->EnableMenuItem(SC_CLOSE, MF_BYCOMMAND|MF_GRAYED);

	ModifyStyle(WS_SYSMENU, 0); 

	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	SetWindowText(m_strCaption);

	CBitmap bitmap;
	bitmap.LoadBitmap(IDB_WATERMARK256);
	m_brush.CreatePatternBrush(&bitmap);
	m_nullBrush.CreateStockObject(NULL_BRUSH);

	CWnd *pWnd = GetDlgItem(IDCANCEL);
	CString strText;
	if (pWnd)
	{
		strText.LoadString(IDS_WIZCANCEL);
		if (strText.IsEmpty())
		{
			strText = _T("Cancel");
		}
		pWnd->SetWindowText(strText);
	}
	pWnd = GetDlgItem(ID_WIZFINISH);
	if (pWnd != NULL)
	{
		strText.LoadString(IDS_WIZFINISH);
		if (strText.IsEmpty())
		{
			strText = _T("&Finish");
		}
		pWnd->SetWindowText(strText);
	}

	return blRet;
}


HBRUSH CSetupSheet::OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor) 
{
	HBRUSH hbr = CSetupSheet_Base::OnCtlColor(pDC, pWnd, nCtlColor);

	m_OldCPalette = pDC->SelectPalette(&m_CPalette, FALSE);
	pDC->RealizePalette();

	// CTLCOLOR_LISTBOX for the combo box list, so it will be white
	// CTLCOLOR_EDIT for the tree ctrl, so it will be white
	if ((nCtlColor == CTLCOLOR_LISTBOX)||(nCtlColor == CTLCOLOR_EDIT))   
	{
		return hbr;
	}

	// use to paint the uid and pid text field, to make 
	// it look tranparents. setbkmode(tranparent) didn't work
	if (nCtlColor == CTLCOLOR_STATIC)
	{
		pDC->SetBkMode(TRANSPARENT);  
	}

	CPoint point;
	SetBrushOrgEx(pDC->m_hDC, -1, -1, &point);

	return (HBRUSH)(m_brush.m_hObject);
}