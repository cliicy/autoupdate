#pragma once

// SetupComponentPage.h : header file

#include "SetupPage.h"
#include "LinkListCtrl.h"
//#include "ProductListCtrl.h"

/////////////////////////////////////////////////////////////////////////////
// CSetupComponentPage dialog

class CSetupComponentPage : public CSetupPage
{
	DECLARE_DYNCREATE(CSetupComponentPage)

// Construction
public:
	CSetupComponentPage();
	~CSetupComponentPage();

// Dialog Data
	//{{AFX_DATA(CSetupComponentPage)
	enum { IDD = IDD_DIALOG_BASE };
	//}}AFX_DATA

protected:

// Overrides
	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(CSetupComponentPage)
	virtual BOOL OnInitDialog();
	virtual BOOL OnSetActive();
	virtual LRESULT OnWizardNext();
	virtual LRESULT OnWizardBack();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL
	DECLARE_MESSAGE_MAP()

// Implementation
	afx_msg void OnBnClickedCheck();

	afx_msg LRESULT OnChangeStatus(WPARAM wParam, LPARAM lParam);

	afx_msg LRESULT OnFinish(WPARAM wParam, LPARAM lParam);

	afx_msg void OnTimer(UINT_PTR nIDEvent);

	void InitListCtrl();

	void ListProducts();

	void UninstallProducts();

	CString GetStatusFromValue(UNINSTALL_STATUS nStatus);

	int GetStatusIcon(UNINSTALL_STATUS nStatus);

	int GetRowByProductName(const CString &strProdName);

	void EnableSheetButton(UINT nID, BOOL blEnable = TRUE);

private:
	CImageList m_objImageList;
	CLinkListCtrl m_ctrList;
	CProgressCtrl m_ctrProgress;
	CStaticEx m_ctrRCStatic;
	LOGFONT m_lf;
	CFont m_font;

	CString m_strPending;
	CString m_strWorking;
	CString m_strCompleted;
	CString m_strFailed;
	CString m_strWorkingMsg;
	CString m_strFinishMsg;
	CString m_strFailedMsg;
};
