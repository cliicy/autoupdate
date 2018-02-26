#pragma once

// SetupComponentPage.h : header file

#include "SetupPage.h"
#include "ProductListCtrl.h"

/////////////////////////////////////////////////////////////////////////////
// CProductListPage dialog

class CProductListPage : public CSetupPage
{
	DECLARE_DYNCREATE(CProductListPage)

// Construction
public:
	CProductListPage();
	~CProductListPage();

// Dialog Data
	//{{AFX_DATA(CProductListPage)
	enum { IDD = IDD_DIALOG_PRODUCT_LIST };
	//}}AFX_DATA

protected:

// Overrides
	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(CProductListPage)
	virtual BOOL OnInitDialog();
	virtual BOOL OnSetActive();
	virtual LRESULT OnWizardNext();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL
	DECLARE_MESSAGE_MAP()

// Implementation
	afx_msg LRESULT OnItemCheboxChanged(WPARAM wParam, LPARAM lParam);

	void InitListCtrl();

	void ListProducts();

	int GetRowByProductName(const CString &strProdName);

	void EnableSheetButton(UINT nID, BOOL blEnable = TRUE);

	void SetToolTip();

	BOOL IsItemSelected();
private:
	//CListCtrl m_ctrList;
	CProductListCtrl m_ctrList;
	CStaticEx m_ctrSCStatic;

	CString m_strTipSelect;
	CString m_strTipDeselect;
};
