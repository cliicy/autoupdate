// SetupLicenseDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Uninstall.h"
#include "SetupComponentPage.h"
#include "SetupSheet.h"

#ifndef UNICODE
#include <io.h>
#endif

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////

#define WM_CHANGE_FONT WM_USER+1000

/////////////////////////////////////////////////////////////////////////////
// CProductListPage property page

IMPLEMENT_DYNCREATE(CProductListPage, CSetupPage)

CProductListPage::CProductListPage() : CSetupPage(CProductListPage::IDD, 0, 0, 0)
{
	m_nNagivageTitleID = IDS_STRING_COMPONENTS;
}

CProductListPage::~CProductListPage()
{
}

void CProductListPage::DoDataExchange(CDataExchange* pDX)
{
	CSetupPage::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CProductListPage)
	DDX_Control(pDX, IDC_LIST_PREREQUISITES, m_ctrList);
	DDX_Control(pDX, IDC_STATIC_SELECTCOMPONENT, m_ctrSCStatic);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CProductListPage, CSetupPage)
	//{{AFX_MSG_MAP(CProductListPage)
	ON_MESSAGE(WM_ITEMCHECKBOXCHANGED, OnItemCheboxChanged)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CProductListPage message handlers

BOOL CProductListPage::OnInitDialog()
{
	CSetupPage::OnInitDialog();	

	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheetEx, pSheet);

	m_strTipSelect.LoadString(IDS_STRING_TIP_SELECT);
	m_strTipDeselect.LoadString(IDS_STRING_TIP_DESELECT);

	CString strMsg;
	strMsg.LoadString(IDS_STRING_APPLICATION_TITLE);
	SetWindowText(strMsg);

	InitListCtrl();

	SetToolTip();
	
	m_ctrSCStatic.SetFontBold(TRUE);

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}


BOOL CProductListPage::OnSetActive()
{
	BOOL blRet = CSetupPage::OnSetActive();

	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	CString strText;

	CWnd *pWnd;

	ListProducts();

	pWnd = pSheet->GetDlgItem(ID_WIZNEXT);
	if (pWnd)
	{
		strText.LoadString(IDS_STRING_WIZ_NEXT);

		if (strText.IsEmpty())
		{
			strText = _T("&Next >");
		}

		pWnd->SetWindowText(strText);

		pWnd->EnableWindow(IsItemSelected());
	}

	pWnd = pSheet->GetDlgItem(IDCANCEL);
	if (pWnd)
	{
		strText.LoadString(IDS_STRING_WIZ_CANCEL);

		if (strText.IsEmpty())
		{
			strText = _T("Cancel");
		}

		pWnd->SetWindowText(strText);
	}

	pWnd = pSheet->GetDlgItem(ID_WIZBACK);
	if (pWnd)
	{
		strText.LoadString(IDS_STRING_WIZ_BACK);

		if (strText.IsEmpty())
		{
			strText = _T("< &Back");
		}

		pWnd->SetWindowText(strText);

		pWnd->EnableWindow(FALSE);
	}

	return blRet;
}

BOOL CProductListPage::IsItemSelected()
{
	int nRows = m_ctrList.GetItemCount();
	for (int nRow = 0; nRow < nRows; nRow++)
	{
		if (m_ctrList.GetCheck(nRow))
		{
			return TRUE;
		}
	}

	return FALSE;
}

LRESULT CProductListPage::OnWizardNext() 
{
	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	int nRows = m_ctrList.GetItemCount();

	INT_PTR nSize = theApp.m_aryProducts.GetSize();

	for (INT_PTR i = 0; i<nSize; i++)
	{
		theApp.m_aryProducts.GetAt(i).m_blSelected = FALSE;
	}

	for (int nRow = 0; nRow < nRows; nRow++)
	{
		CString strProdName = m_ctrList.GetItemText(nRow, 0);

		int nIndex = theApp.GetIndexByProductName(strProdName);
		if (nIndex >= 0)
		{
			theApp.m_aryProducts.GetAt(nIndex).m_blSelected = m_ctrList.GetCheck(nRow);
		}
	}

	return CSetupPage::OnWizardNext();
}


void CProductListPage::ListProducts()
{
	m_ctrList.DeleteAllItems();
	CString strText,strProudctNameEx;
	strText.LoadString(IDS_STRING_INSTALLED);

	int j = 0;
	for (int i=0; i<theApp.m_aryProducts.GetSize(); i++)
	{
		if (theApp.m_aryProducts[i].m_dwComponentType == CAPRODUCT && theApp.m_aryProducts[i].m_nStatus != M_STATUS_COMPLETED)
		{
			//align this string for herder string.
			strProudctNameEx.Format(_T(" %s"),theApp.m_aryProducts[i].m_strProductName);
			m_ctrList.InsertItem(j,strProudctNameEx);
			m_ctrList.SetItemText(j,1,strText);
			j++;
		}
	}

	//Select the default product
	INT_PTR nSize = theApp.m_aryProducts.GetSize();
	for (int i=0; i<nSize; i++)
	{
		if (theApp.m_aryProducts.GetAt(i).m_blSelected)
		{
			int nRow = GetRowByProductName(theApp.m_aryProducts.GetAt(i).m_strProductName);
			if (nRow >= 0)
			{
				m_ctrList.SetCheck(nRow, TRUE);
			}
		}
	}
}


void CProductListPage::InitListCtrl()
{
	CString strText;

	//Insert Columns
	strText.LoadString(IDS_PREREQUISITES_NAME);
	m_ctrList.InsertColumn(0, strText, LVCFMT_LEFT, 405);

	strText.LoadString(IDS_PREREQUISITES_STATUS);
	m_ctrList.InsertColumn(1, strText, LVCFMT_LEFT, 125);

	m_ctrList.Init();
	
	m_ctrList.SetExtendedStyle(m_ctrList.GetExtendedStyle() | LVS_EX_CHECKBOXES /*| LVS_EX_GRIDLINES*/ | LVS_EX_FULLROWSELECT);
}

LRESULT CProductListPage::OnItemCheboxChanged(WPARAM wParam, LPARAM lParam)
{
	BOOL bChecked = (lParam != 0);
	
	int nItem = (int)wParam;
	if (nItem < 0 || nItem >= m_ctrList.GetItemCount())
		return 0;

	CString strProdName = m_ctrList.GetItemText(nItem, 0);

	int i;
	int nArrayIndex = theApp.GetIndexByProductName(strProdName);
	INT_PTR nArraySize = theApp.m_aryProducts.GetSize();

	if (nArrayIndex >= 0)
	{
		CString strShortName = theApp.m_aryProducts.GetAt(nArrayIndex).m_strShortName;

		if (bChecked)
		{
			for (i=0; i<nArraySize; i++)
			{
				for (int j=0; j<theApp.m_aryProducts.GetAt(i).m_strDependentFeatures.GetSize(); j++)
				{
					if (theApp.m_aryProducts.GetAt(i).m_strDependentFeatures[j].CompareNoCase(strShortName) == 0)
					{
						int nListIndex = GetRowByProductName(theApp.m_aryProducts.GetAt(i).m_strProductName);
						if (nListIndex >= 0)
						{
							m_ctrList.SetCheck(nListIndex, TRUE);
						}
					}
				}
			}

			EnableSheetButton(ID_WIZNEXT, TRUE);
		}
		else
		{
			for (i=0; i<nArraySize; i++)
			{
				for (int j=0; j<theApp.m_aryProducts.GetAt(nArrayIndex).m_strDependentFeatures.GetSize(); j++)
				{
					if (theApp.m_aryProducts.GetAt(nArrayIndex).m_strDependentFeatures[j].CompareNoCase(theApp.m_aryProducts.GetAt(i).m_strShortName) == 0)
					{
						int nListIndex = GetRowByProductName(theApp.m_aryProducts.GetAt(i).m_strProductName);
						if (nListIndex >= 0)
						{
							m_ctrList.SetCheck(nListIndex, FALSE);
						}
						break;
					}
				}
			}


			EnableSheetButton(ID_WIZNEXT, IsItemSelected());
		}
	}

	SetToolTip();

	return 0;
}

void CProductListPage::SetToolTip()
{
	CHeaderCtrl *pHead = m_ctrList.GetHeaderCtrl();
	if (pHead)
	{
		HDITEM hdItem;
		hdItem.mask = HDI_IMAGE;
		pHead->GetItem(0, &hdItem);

		CString strTip;
		if (hdItem.iImage == CHECK)
		{
			m_ctrList.AddHeaderToolTip(0, m_strTipDeselect);
		}
		else
		{
			m_ctrList.AddHeaderToolTip(0, m_strTipSelect);
		}
	}
}

int CProductListPage::GetRowByProductName(const CString &strProdName)
{
	int nSize = m_ctrList.GetItemCount();
	CString strText;
	for (int i=0; i<nSize; i++)
	{
		strText = m_ctrList.GetItemText(i, 0);
		strText.TrimLeft();
		if (strText == strProdName)
		{
			return i;
		}
	}

	return -1;
}


void CProductListPage::EnableSheetButton(UINT nID, BOOL blEnable)
{
	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	CWnd *pWnd = pSheet->GetDlgItem(nID);
	if (pWnd)
	{
		pWnd->EnableWindow(blEnable);
	}
}